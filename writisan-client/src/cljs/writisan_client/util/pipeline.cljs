(ns writisan-client.util.pipeline
  (:require [cljs.core.async :refer [<! chan put!]]
            [promesa.core :as p]
            [promesa.impl.proto :refer [IPromise]]
            [keechma.controller :as controller])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defprotocol IPipeline
  (stop! [pipeline] "Stops the pipeline")
  (ok? [pipeline] "Is pipeline in the `ok` state")
  (value [pipeline] "Returns the current value")
  (send-command! [this command-name] [this command-name args] "Sends the command through the controller")
  (commit! [this app-state] "Commits app state value")
  (redirect! [this params] "Redirects to a different URL"))

(defrecord Pipeline [action state sideffects ctx args]
  IPipeline
  (stop! [this]
    (assoc this :action :stop))
  (ok? [this]
    (= :ok (first (:state this))))
  (value [this]
    (last (:state this)))
  (send-command! [this command-name]
    (send-command! this command-name nil))
  (send-command! [this command-name args]
    (assoc-in this [:sideffects :commands]
              (conj (or (get-in this [:sideffects :commands]) []) [command-name args])))
  (commit! [this app-state]
    (assoc-in this [:sideffects :commit] app-state))
  (redirect! [this params]
    (assoc-in this [:sideffects :redirect] params)))

(defn init-pipeline [args]
 (map->Pipeline 
  {:action :next
   :state [:ok nil]
   :sideffects {}
   :ctx {}
   :args args}))

(defn promise->chan [promise]
  (let [promise-chan (chan)]
    (->> promise
         (p/map (fn [v] (put! promise-chan [:ok v])))
         (p/error (fn [e] (put! promise-chan [:error e]))))
    promise-chan))

(defn run-pipeline [ctrl app-db-atom actions args]
  (go-loop [a actions
            p (init-pipeline args)]
    (let [next (first a)
          ret-val (or (next p @app-db-atom) p)
          has-next? (< 1 (count a))
          was-promise? (satisfies? IPromise ret-val)
          [status res] (<! (promise->chan (p/promise ret-val)))
          next-p (if (satisfies? IPipeline res) res (assoc p :state [status res]))
          commit (get-in next-p [:sideffects :commit])
          commands (get-in next-p [:sideffects :commands])]
      (if (and was-promise? commit)
        (throw "You're trying to commit the app-state from an async function. There is a chance that app-state was modified in the meantime!")
        (do
          (when commit
            (reset! app-db-atom commit))
          (map (fn [command]
                 (controller/send-command ctrl (first command) (last command))) commands)
          (when-let [params (get-in next-p [:sideffects :redirect])]
            (controller/redirect ctrl params))
          (when (and has-next? (= (:action next-p) :next))
            (recur (drop 1 a)
                   (assoc next-p :sideffects {}))))))))
