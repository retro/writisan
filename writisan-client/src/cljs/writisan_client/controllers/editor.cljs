(ns writisan-client.controllers.editor
  (:require [writisan-client.util.dispatch-controller :as dispatch-controller]
            [writisan-client.util.pipeline :as pp]
            [writisan-client.edb :as edb]
            [writisan-client.api :refer [token save-document]]
            [clojure.string :refer [trim]])
  (:require-macros [writisan-client.util.pipeline :refer [pipeline->]]))

(defn insert-document [app-db value meta]
  (edb/insert-named-item app-db :documents :current value meta))

(defn clear-error [app-db]
  (let [val (edb/get-named-item app-db :documents :current)]
    (insert-document app-db val {})))

(defn editor-page? [route]
  (when (= "editor" (get-in route [:data :page])) true))

(def editor-actions
  {:save (pipeline-> 
          (begin [value app-db]
                 (when (empty? (trim value))
                   (pp/error! :validation "Empty document can't be saved"))
                 (pp/commit! (insert-document app-db {} {:is-saving? true}))
                 (save-document value (token app-db))
                 (pp/commit! (insert-document app-db value {}))
                 (pp/redirect! {:page "comments" :id (:hash value)}))

          (rescue [error value app-db]
                  (when (= (:type error) :validation) 
                    (pp/commit! (insert-document app-db {} {:error (:payload error)})))))

   :clear-error (fn [_ app-db-atom _] (reset! app-db-atom (clear-error @app-db-atom)))})

(def editor-controller (dispatch-controller/constructor editor-page? editor-actions))
