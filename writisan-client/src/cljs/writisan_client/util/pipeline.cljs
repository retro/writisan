(ns writisan-client.util.pipeline
  (:require [cljs.core.async :refer [<! chan put!]]
            [promesa.core :as p]
            [promesa.impl.proto :refer [IPromise]]
            [keechma.controller :as controller])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defrecord Error [type message payload cause])

(defn error! [type payload]
  (->Error type nil payload nil))

(defprotocol ISideffect
  (do! [this controller app-db-atom]))

(defrecord CommitSideffect [value]
  ISideffect
  (do! [this _ app-db-atom]
    (reset! app-db-atom (:value this))))

(defrecord SendCommandSideffect [command payload]
  ISideffect
  (do! [this controller _]
    (controller/send-command controller (:command this) (:payload this))))

(defrecord ExecuteSideffect [payload]
  ISideffect
  (do! [this controller _]
    (controller/execute controller (:payload this))))

(defrecord RedirectSideffect [params]
  ISideffect
  (do! [this controller _]
    (controller/redirect controller params)))

(defn commit! [value]
  (->CommitSideffect value))

(defn execute! [value]
  (->ExecuteSideffect value))

(defn send-command! [command payload]
  (->SendCommandSideffect command payload))

(defn redirect! [params]
  (->RedirectSideffect params))

(defn process-error [err]
  (cond
    (instance? Error err) err
    :else (->Error :default nil err nil)))

(defn action-ret-val [action value error app-db]
  (try
    (let [ret-val (if (nil? error) (action value app-db) (action error value app-db))]
      {:value ret-val
       :promise? (satisfies? IPromise ret-val)})
    (catch :default err
      (cond
        (or (instance? ExceptionInfo err) (instance? js/Error err)) (throw err)
        :else  {:value (process-error err)
                :promise? false}))))

(defn promise->chan [promise]
  (let [promise-chan (chan)]
    (->> promise
         (p/map (fn [v] (put! promise-chan (if (nil? v) ::nil v))))
         (p/error (fn [e] (put! promise-chan (process-error e)))))
    promise-chan))

(def pipeline-errors
  {:async-sideffect "Returning sideffects from promises is not permitted. It is possible that application state was modified in the meantime"
   :rescue-missing "Unable to proceed with the pipeline. Rescue block is missing."
   :rescue-errors "Unable to proceed with the pipeline. Error was thrown in rescue block"})

(defn run-pipeline [pipeline ctrl app-db-atom value]
  (let [{:keys [begin rescue]} pipeline]
    (go-loop [actions begin
              val value
              error nil
              running :begin]
      (when (pos? (count actions))
        (let [next (first actions)
              {:keys [value promise?]} (action-ret-val next val error @app-db-atom)
              resolved (<! (promise->chan (p/promise value)))
              resolved-value (if (= ::nil resolved) nil resolved)
              sideffect? (satisfies? ISideffect resolved-value)
              error? (instance? Error resolved-value)]
          (when (and promise? sideffect?)
            (throw (ex-info (:async-sideffect pipeline-errors) {})))
          (when sideffect?
            (do
              (do! resolved-value ctrl app-db-atom)))
          (cond
            (and error? (= running :begin)) (if (pos? (count rescue))
                                              (recur rescue val resolved-value :rescue)
                                              (throw (ex-info (:rescue-missing pipeline-errors) resolved-value)))
            (and error? (= running :rescue)) (throw (ex-info (:rescue-error pipeline-errors) resolved-value))
            sideffect? (recur (drop 1 actions) val error :begin)
            :else (recur (drop 1 actions) (if (nil? resolved-value) val resolved-value) error :begin)))))))

(defn make-pipeline [pipeline]
  (partial run-pipeline pipeline))
