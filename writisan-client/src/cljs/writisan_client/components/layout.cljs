(ns writisan-client.components.layout
  (:require [keechma.ui-component :as ui]
            [writisan-client.util :refer [gravatar-url]]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [writisan-client.stylesheets.core :refer-macros [defelement]]))

(defelement layout-container
  :style {:padding-top "51px"}
  :class [:container])

(defelement header
  :style {:border-bottom (str "1px solid " (:silver-d colors-with-variations))}
  :class [:bg-silver.c-black-l.fixed.top-0.left-0.right-0.z1]
  :tag :header)

(defelement avatar-img
  :style {:width "36px"
          :margin-top "-9px"
          :margin-bottom "-9px"}
  :class [:circle.border.bw2.bd-white.right.ml1]
  :tag :img)

(defelement logo-img
  :style {:height "18px"
          :margin-top "16px"}
  :tag :img)

(defelement user
  :class [:.right.h5.bg-silver-d.p2])

(defn user-avatar-url [user]
  (or (:avatar user)
      (gravatar-url (:email user))))

(defn render-current-user [user]
  (when user
    [-user
     [:span.c-white (:name user)]
     [-avatar-img {:src (user-avatar-url user)}]]))

(defn render [ctx]
  (fn []
    (let [page @(ui/subscription ctx :page)]
      [:div
       [-header
        [:div.container
         [:div.left
          [-logo-img {:src "/images/writisan-logo-small.png"}]]
         (render-current-user @(ui/subscription ctx :current-user))]]
       [-layout-container
        (when (= page "editor")
          [(ui/component ctx :editor)])]])))

(def component (ui/constructor {:renderer render
                                :component-deps [:editor]
                                :subscription-deps [:current-user :page]}))
