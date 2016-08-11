(ns writisan-client.ui-system
  (:require [writisan-client.components.layout :as layout]
            [writisan-client.components.editor :as editor]))

(def system
  {:main layout/component
   :editor editor/component})
