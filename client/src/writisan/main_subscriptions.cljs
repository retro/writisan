(ns writisan.main-subscriptions
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn page [app-db-atom]
  (reaction
   (get-in @app-db-atom [:route :data :page])))

(def subscriptions
  {:page page})
