(ns writisan-client.controllers.setup
  (:require [keechma.controller :as controller]
            [writisan-client.edb :as edb]))

(defn persist-user-data [app-db]
  (-> app-db
      (edb/insert-named-item :users :current (js->clj (.. js/window -USER -data) :keywordize-keys true))
      (assoc-in [:kv :token] (.. js/window -USER -token))))

(defrecord Controller []
  controller/IController
  (params [_ _]
    true)
  (start [_ params app-db]
    (persist-user-data app-db)))
