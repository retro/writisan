(ns writisan.components.comment
  (:require [keechma.ui-component :as ui]
            [cljsjs.moment]))
(defn format-time [timestamp]
  (.format (js/moment timestamp) "DD.MM.YYYY HH:mm"))

(defn render [ctx comment] 
  (fn []
    (let [post-users-sub (ui/subscription ctx :post-users)
          post-users @post-users-sub
          user (get post-users (:postedById comment))]
      (when user
        [:div.comment-component
         [:div.author
          [:img {:src (:image user)}]
          (str (:name user) " on " (format-time (:createdAt comment)))]
         [:div.body
          (:text comment)]]))))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:post-users]}))
