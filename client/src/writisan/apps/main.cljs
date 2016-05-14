(ns writisan.apps.main
  (:require [writisan.components.main :as main-c]
            
            ))


(def app {:routes ["session/:session-action"]
          :controllers {}
          :components {:main main-c/component}
          :subscriptions {}})
