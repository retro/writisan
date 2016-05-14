(ns writisan.main-subscriptions
  (:require [writisan.edb :as edb])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn page [app-db-atom]
  (reaction
   (get-in @app-db-atom [:route :data :page])))

(defn current-post [app-db-atom]
  (reaction
   (edb/get-named-item @app-db-atom :posts :current)))

(defn current-comment-form-idx [app-db-atom]
  (reaction
   (get-in @app-db-atom [:kv :current-comment-form-idx])))

(defn comments [app-db-atom]
  (reaction
   (edb/get-collection @app-db-atom :comments :list)))

(defn post-users [app-db-atom]
  (reaction
   (let [users (edb/get-collection @app-db-atom :post-users :list)
         _ (.log js/console (clj->js users))]
     (reduce (fn [acc user] (assoc acc (:_id user) user)) {} users))))

(def subscriptions
  {:page page
   :current-comment-form-idx current-comment-form-idx
   :comments comments
   :post-users post-users
   :current-post current-post})
