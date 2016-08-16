(ns writisan-client.subscriptions
  (:require [writisan-client.edb :as edb])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn page [app-db-atom]
  (reaction
   (get-in @app-db-atom [:route :data :page])))

(defn current-document [app-db-atom]
  (reaction
   (let [db @app-db-atom
         data (edb/get-named-item db :documents :current)
         meta (edb/get-named-item-meta db :documents :current)]
     {:meta meta
      :data data})))

(defn current-comment-form-idx [app-db-atom]
  (reaction
   (get-in @app-db-atom [:kv :current-comment-form-idx])))

(defn is-saving-document? [app-db-atom]
  (reaction
   (:is-saving? (edb/get-named-item-meta @app-db-atom :documents :current))))

(defn current-user [app-db-atom]
  (reaction
   (edb/get-named-item @app-db-atom :users :current)))

(def subscriptions
  {:page page
   :current-comment-form-idx current-comment-form-idx
   :current-document current-document
   :is-saving-document? is-saving-document?
   :current-user current-user})
