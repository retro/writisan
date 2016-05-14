(ns writisan.session-subscriptions
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn current-user [app-db]
  ;; Returns the current user. Called by the `main` app
  (reaction
   (get-in @app-db [:kv :current-user])))

(defn main-app [app-db]
  (reaction
   (get-in @app-db [:kv :main-app])))

(defn login-status [app-db]
  (reaction
   (get-in @app-db [:kv :login-status])))

(def subscriptions
  {:login-status login-status
   :main-app main-app})
