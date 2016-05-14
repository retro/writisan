(ns writisan.controllers.comments
 (:require [writisan.services :refer [comments-service create-item find-items]]
           [keechma.controller :as controller]
           [clojure.string :as string]
           [writisan.edb :as edb]) )

(defn save-comment [app-db-atom comment]
  (let [app-db @app-db-atom
        post-id (:_id (edb/get-named-item app-db :posts :current))
        current-idx (get-in app-db [:kv :current-comment-form-idx])]
    (when (not (empty? (string/trim comment)))
      (create-item
       comments-service
       #js{:text comment
           :idx current-idx
           :postId post-id}
       (fn [item]
         (swap! app-db-atom assoc-in [:kv :current-comment-form-idx] nil))))))

(defn load-comments [app-db-atom current-post-id]
  (let [app-db @app-db-atom]
    (find-items comments-service
                #js{:postId current-post-id :$limit 100}
                #(reset! app-db-atom (edb/insert-collection @app-db-atom :comments :list (:data %))))))

(defn listen-created [app-db-atom post-id]
  (.on comments-service "created"
       (fn [data]
         (let [converted (js->clj data :keywordize-keys true)]
           (when (= (:postId converted) post-id)
             (reset! app-db-atom
                     (edb/append-collection @app-db-atom :comments :list [converted])))))))

(defrecord Controller []
  controller/IController
  (params [_ route-params]
    (when (= (get-in route-params [:data :page]) "comments")
      (get-in route-params [:data :id]))) 
  (start [this id app-db]
    (controller/execute this :load id)
    (controller/execute this :listen-created id)
    (edb/insert-collection app-db :comments :list []))
  (handler [this app-db-atom in-chan _]
    (let []
      (controller/dispatcher app-db-atom in-chan
                             {:create save-comment
                              :listen-created listen-created
                              :load load-comments}))))
