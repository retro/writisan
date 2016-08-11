(ns writisan-client.components.layout
  (:require [keechma.ui-component :as ui]
            [writisan-client.util :refer [gravatar-url]]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]))

(defn stylesheet []
  [[:.layout--container {:padding-top "51px"}]
   [:.layout--header {:border-bottom (str "1px solid " (:silver-d colors-with-variations))}]
   [:.layout--avatar {:width "36px"
                      :margin-top "-9px"
                      :margin-bottom "-9px"}]
   [:.layout--logo {:height "18px"
                    :margin-top "16px"}]])

(defn user-avatar-url [user]
  (or (:avatar user)
      (gravatar-url (:email user))))

(defn render-current-user [user]
  (when user
    [:div.right.h5.bg-silver-d.p2
     [:span.c-white (:name user)]
     [:img.circle.border.bw2.bd-white.right.ml1.layout--avatar {:src (user-avatar-url user)}]]))

(defn render [ctx]
  (fn []
    [:div
     [:header.bg-silver.c-black-l.fixed.top-0.left-0.right-0.z1.layout--header
      [:div.container
       [:div.left
        [:img.layout--logo {:src "/images/writisan-logo-small.png"}]]
       (render-current-user @(ui/subscription ctx :current-user))]]
     [:div.container.layout--container
      [(ui/component ctx :editor)]]]))

(def component (ui/constructor {:renderer render
                                :component-deps [:editor]
                                :subscription-deps [:current-user]}))
