(ns writisan.apps.main
  (:require [writisan.main-ui-system :as ui-system]
            [writisan.controllers.editor :as editor]
            [writisan.controllers.feedback :as feedback]
            [writisan.controllers.comments :as comments]
            [writisan.controllers.post-users :as post-users]
            [writisan.main-subscriptions :as subs]))


(def app {:routes [["" {:page "editor"}]
                   ":page/:id"
                   "session/:session-action"]
          :controllers {:editor (editor/->Controller)
                        :post-users (post-users/->Controller)
                        :feedback (feedback/->Controller)
                        :comments (comments/->Controller)}
          :components ui-system/system
          :subscriptions subs/subscriptions})
