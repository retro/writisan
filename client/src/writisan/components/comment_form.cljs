(ns writisan.components.comment-form
  (:require [keechma.ui-component :as ui]
            [reagent.core :as reagent]))

(defn render [ctx]
  (let [comment (reagent/atom "")]
    (fn []
      [:div.comment-form.cf
       [:div.title "Give feedback for this section"]
       [:textarea
        {:value @comment
         :on-change #(reset! comment (.. % -target -value))}]
       [:button.add-comment
        {:on-click #(ui/send-command ctx :create @comment)}
        "Send Feedback"]])))

(def component
  (ui/constructor {:renderer render
                   :topic :comments}))
