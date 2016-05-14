(ns writisan.controllers.login
  (:require [keechma.controller :as controller]
            [keechma.app-state :as app-state]))

(defn start-app!
  [main-app app-db-atom]
  (let [started-app (app-state/start! main-app false)
        app-db @app-db-atom]
    (reset! app-db-atom
            (-> app-db
                (assoc-in [:kv :main-app] started-app)
                (assoc-in [:kv :login-status] :auth)))))

(defn login
  [main-app app-db-atom _]
  (let [success-cb (partial start-app! main-app app-db-atom)]
    (js/AUTH success-cb #{swap! app-db-atom assoc-in [:kv :login-status] :error})))

(defn authenticate [app-db-atom main-app]
  (.then
   (.authenticate js/F)
   (partial start-app! main-app app-db-atom)
   (fn [] (swap! app-db-atom assoc-in [:kv :login-status] :anon))))


(defrecord ^{:doc "
Handles the user login.

When the user clicks the \"Login\" button it will use the Trello
client to create the session.

After that it will start the `main` application and save the
reference to it in the `session` app's application state.

This allows the `session` app to render the main application instead
of the login screen.
"} Controller [main-app]
  controller/IController
  (params [_ route-params] true)
  (start [this params app-db]
    (assoc-in app-db [:kv :login-status] :loading))
  (handler [this app-db-atom in-chan _]
    (authenticate app-db-atom main-app)
    (let [main-app (:main-app this)]
      (controller/dispatcher app-db-atom in-chan
                             {:login (partial login main-app)}))))
