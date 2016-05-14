(ns writisan.core
  (:require [keechma.app-state :as app-state]
            [writisan.apps.session :as session]))

(enable-console-print!)

(defonce running-app (clojure.core/atom))

(defn start-app!
  "Helper function that starts the application."
  []
  (reset! running-app (app-state/start! session/app)))

(defn restart-app!
  "Helper function that restarts the application whenever the
  code is hot reloaded."
  []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(restart-app!)
