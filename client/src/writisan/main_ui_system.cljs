(ns writisan.main-ui-system
  (:require [writisan.components.editor :as editor]
            [writisan.components.comment :as comment]
            [writisan.components.comment-form :as comment-form]
            [writisan.components.feedback :as feedback]
            [writisan.components.main :as main]))

(def system
  {:main main/component
   :feedback feedback/component
   :comment-form comment-form/component
   :comment comment/component
   :editor editor/component})
