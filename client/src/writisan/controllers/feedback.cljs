(ns writisan.controllers.feedback
  (:require [writisan.services :refer [posts-service get-item]]
            [keechma.controller :as controller]
            [writisan.edb :as edb]))

(defn load-post [app-db-atom id]
  (get-item posts-service id
            (fn [item]
              (reset! app-db-atom (edb/insert-named-item @app-db-atom :posts :current item)))))


(defrecord Controller []
  controller/IController
  (params [_ route-params]
    (when (= (get-in route-params [:data :page]) "comments")
      (get-in route-params [:data :id]))) 
  (start [this id app-db]
    (let [post (edb/get-item-by-id app-db :posts id)]
      (if post
        (edb/insert-named-item app-db :posts :current post)
        (do
          (controller/execute this :load-post id)
          app-db))))
  (handler [this app-db-atom in-chan _]
    (controller/dispatcher app-db-atom in-chan
     {:load-post load-post})))

