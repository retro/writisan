(ns writisan.apps.main
  (:require [writisan.main-ui-system :as ui-system]
            [writisan.controllers.editor :as editor]
            [writisan.controllers.feedback :as feedback]
            [writisan.main-subscriptions :as subs]))


(def app {:routes [["" {:page "editor"}]
                   ":page/:id"
                   "session/:session-action"]
          :controllers {:editor (editor/->Controller)
                        :feedback (feedback/->Controller)}
          :components ui-system/system
          :subscriptions subs/subscriptions})
