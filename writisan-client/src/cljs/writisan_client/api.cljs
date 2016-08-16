(ns writisan-client.api
  (:require [ajax.core :refer [POST]]
            [promesa.core :as p]))

(defn token [app-db]
  (get-in app-db [:kv :token]))

(defn req->promise [action token url data]
  (p/promise
   (fn [resolve reject]
     (action url (merge {:handler resolve
                         :error-handler reject
                         :response-format :json
                         :keywords? true
                         :format :json
                         :headers {:Authorization token}} data)))))

(defn save-document [document token]
  (->> (req->promise POST token "/api/v1/documents"
                     {:params {:document {:content document}}})
       (p/map :data)))
