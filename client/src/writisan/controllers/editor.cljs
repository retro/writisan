(ns writisan.controllers.editor
  (:require [keechma.controller :as controller]
            [ajax.core :refer [POST json-response-format]]
            [writisan.services :refer [posts-service create-item]]
            [writisan.edb :as edb]))


(defn save-article [success-redirect app-db-atom article]
  (swap! app-db-atom assoc-in [:kv :is-saving-article] true)
  (create-item
   posts-service
   #js{:text article}
   (fn [item]
     (reset! app-db-atom
             (-> @app-db-atom
                 (assoc-in [:kv :is-saving-article] false)
                 (edb/insert-named-item :posts :current item))) 
     (success-redirect (:_id item)))))

(defrecord Controller []
  controller/IController
  (params [_ route-params]
    (when (= (get-in route-params [:data :page]) "editor")
      true)) 
  (handler [this app-db-atom in-chan _]
    (let [main-app (:main-app this)
          success-redirect (fn [id] (controller/redirect this {:page "comments" :id id}))]
      (controller/dispatcher app-db-atom in-chan
                             {:save-article (partial save-article success-redirect)}))))
