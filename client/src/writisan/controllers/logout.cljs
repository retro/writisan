(ns writisan.controllers.logout
  (:require [keechma.controller :as controller]
            [keechma.app-state :as app-state]))

(defrecord ^{:doc "
When the user lands on the logout url, and this controller is started,
it will immediately redirect the user to the frontpage URL.

Logout is handled by the `stop` function which will stop the main
application and remove it from the `session` app's application state.

This will cause the `session` app to render the login screen.
"} Controller []
  controller/IController
  (params [_ route-params]
    (when (= (get-in route-params [:data :session-action]) "logout")
      true))
  (start [this params app-db]
    (controller/redirect this {})
    app-db)
  (stop [_ params app-db]
    (let [kv-store (:kv app-db)
          main-app (:main-app kv-store)]
      (app-state/stop! main-app)
      (assoc app-db :kv (dissoc kv-store :main-app)))))
