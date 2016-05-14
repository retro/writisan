(ns writisan.apps.session
  (:require [keechma.controller :as c]
            [writisan.controllers.login :as login]
            [writisan.controllers.logout :as logout]
            [writisan.components.session :as session-c]
            [writisan.subscriptions :as subs]
            [writisan.apps.main :as main]))

(def app {:routes ["session/:session-action"]
          :controllers {:login (login/->Controller main/app)
                        :logout (logout/->Controller)}
          :components {:main session-c/component}
          :subscriptions subs/subscriptions
          :html-element (.getElementById js/document "app")})
