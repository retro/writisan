(ns writisan.controllers.editor
  (:require [keechma.controller :as controller]
            [ajax.core :refer [POST json-response-format]]
            [writisan.services :refer [posts-service create-item]]
            [writisan.edb :as edb]))


(defn save-article [success-redirect app-db-atom article]
  (create-item
   posts-service
   #js{:text article}
   (fn [item]
     (edb/insert-named-item @app-db-atom :posts :current item)
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
