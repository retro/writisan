(ns writisan-client.core
  (:require-macros
   [reagent.ratom :refer [reaction]])
  (:require
   [devtools.core :as devtools]
   [reagent.core :as reagent]
   [keechma.app-state :as app-state]
   [writisan-client.stylesheets.core :refer [stylesheet]]
   [writisan-client.util :refer [update-page-css]]
   [writisan-client.ui-system :as ui]
   [writisan-client.subscriptions :as subs]
   [writisan-client.controllers.setup :as c-setup]))

(defonce debug?
  ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)))

(def app-definition
  {:components    ui/system
   :controllers   {:setup (c-setup/->Controller)}
   :subscriptions subs/subscriptions
   :html-element  (.getElementById js/document "app")})

(defonce running-app (clojure.core/atom))

(defn start-app! []
  (reset! running-app (app-state/start! app-definition))
  (update-page-css (stylesheet)))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn ^:export main []
  (dev-setup)
  (start-app!))
