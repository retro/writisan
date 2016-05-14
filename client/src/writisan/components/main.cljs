(ns writisan.components.main
  (:require [keechma.ui-component :as ui]))

(defn render [ctx]
  (fn []
    (let [page-sub (ui/subscription ctx :page)
          page @page-sub]
      [:div.layout {:class (when (= page "editor") "layout--editing")}
       [:div.main
        (case page
          "editor" [(ui/component ctx :editor)]
          "comments" [:div.feedback-component 
                      [:div.post-block "aaa"]
                      [:div.comments-block "bbb"]]
          [:div "Page doesn't exist"])]])))

(def component (ui/constructor
                {:renderer render
                 :component-deps [:editor]
                 :subscription-deps [:page]}))

