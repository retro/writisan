(ns writisan.controllers.post-users
 (:require [writisan.services :refer [post-users-service find-items]]
           [keechma.controller :as controller]
           [clojure.string :as string]
           [writisan.edb :as edb]) )

(defn load-post-users [app-db-atom current-post-id]
  (let [app-db @app-db-atom]
    (find-items post-users-service
                #js{:postId current-post-id :$limit 100}
                #(reset! app-db-atom (edb/insert-collection @app-db-atom :post-users :list %)))))

(defrecord Controller []
  controller/IController
  (params [_ route-params]
    (when (= (get-in route-params [:data :page]) "comments")
      (get-in route-params [:data :id]))) 
  (start [this id app-db]
    (controller/execute this :load id)
    (edb/insert-collection app-db :post-users :list []))
  (handler [this app-db-atom in-chan _]
    (let []
      (controller/dispatcher app-db-atom in-chan
                             {:load load-post-users}))))
