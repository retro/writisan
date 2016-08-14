(ns writisan-client.controllers.editor
  (:require [writisan-client.util.pipeline-controller :as pipeline-controller]
            [writisan-client.util.pipeline :as pp]
            [writisan-client.edb :as edb]
            [writisan-client.api :refer [token save-document]]
            [clojure.string :refer [trim]])
  (:require-macros [writisan-client.util.pipeline :refer [p->]]))

(defn insert-document [app-db value meta]
  (edb/insert-named-item app-db :documents :current value meta))

(defn editor-page? [route]
  (when (= "editor" (get-in route [:data :page]))
    true))


(def editor-actions
  {:save (p-> [pipeline app-db]
              (when (empty? (trim (:args pipeline)))
                (-> pipeline
                    (pp/commit! (insert-document app-db {} {:error "can't be empty"}))
                    (pp/stop!)))
              (pp/commit! pipeline (insert-document app-db {} {:is-saving? true}))
              (save-document (:args pipeline) (token app-db))
              (:data (pp/value pipeline))
              (if (pp/ok? pipeline)
                (pp/commit! pipeline (insert-document app-db (pp/value pipeline) {}))
                (-> pipeline
                    (pp/commit! (insert-document app-db {} {:error (pp/value pipeline)}))
                    (pp/stop!)))
              (pp/redirect! pipeline {:page "comments" :id (:hash (pp/value pipeline))}))})

(def editor-controller (pipeline-controller/constructor editor-page? editor-actions))
