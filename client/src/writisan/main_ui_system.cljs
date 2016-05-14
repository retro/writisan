(ns writisan.main-ui-system
  (:require [writisan.components.editor :as editor]
            [writisan.components.main :as main]))

(def system
  {:main main/component
   :editor editor/component})
