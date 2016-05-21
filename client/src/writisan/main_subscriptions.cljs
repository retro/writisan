(ns writisan.main-subscriptions
  (:require [writisan.edb :as edb])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn page [app-db-atom]
  (reaction
   (get-in @app-db-atom [:route :data :page])))

(defn current-post [app-db-atom]
  (reaction
   (let [db @app-db-atom
         data (edb/get-named-item db :posts :current)
         meta (edb/get-named-item-meta :posts :current)]
     {:meta meta
      :data data})))


(defn current-comment-form-idx [app-db-atom]
  (reaction
   (get-in @app-db-atom [:kv :current-comment-form-idx])))

(defn comments [app-db-atom]
  (reaction
   (let [db @app-db-atom
         data (edb/get-collection db :comments :list)
         meta (edb/get-collection-meta db :comments :list)]
     {:meta meta
      :data data})))

(defn post-users [app-db-atom]
  (reaction
   (let [users (edb/get-collection @app-db-atom :post-users :list)]
     (reduce (fn [acc user] (assoc acc (:_id user) user)) {} users))))

(defn is-saving-article? [app-db-atom]
  (reaction
   (get-in @app-db-atom [:kv :is-saving-article])))

(def subscriptions
  {:page page
   :current-comment-form-idx current-comment-form-idx
   :comments comments
   :post-users post-users
   :is-saving-article? is-saving-article?
   :current-post current-post})
