(ns writisan.components.comment-form
  (:require [keechma.ui-component :as ui]
            [reagent.core :as reagent]))

(defn render [ctx]
  (let [comment (reagent/atom "")
        is-sending? (reagent/atom false)
        save-comment (fn []
                       (reset! is-sending? true)
                       (ui/send-command ctx :create @comment))]
    (fn []
      [:div.comment-form.cf
       [:div.title "Give feedback for this section"]
       [:textarea
        {:value @comment
         :read-only @is-sending?
         :on-change #(reset! comment (.. % -target -value))}]
       [:button.add-comment
        {:on-click save-comment
         :disabled @is-sending?}
        (when @is-sending?
          [:i.fa.fa-circle-o-notch.fa-spin])
        "Send Feedback"]])))

(def component
  (ui/constructor {:renderer render
                   :topic :comments}))
