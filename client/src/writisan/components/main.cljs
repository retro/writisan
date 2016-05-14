(ns writisan.components.main
  (:require [keechma.ui-component :as ui]))

(defn render [ctx]
  (fn []
    [:div "MAIN APP"]))

(def component (ui/constructor
                {:renderer render
                 
                 }))

