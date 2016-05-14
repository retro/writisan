(ns writisan.components.feedback
  (:require [keechma.ui-component :as ui]))

(defn group-comments [comments]
  (reduce (fn [acc comment]
            (let [idx (:idx comment)
                  current-list (or (get acc idx) [])]
              (assoc acc idx (conj current-list comment)))) {} comments))

(defn render-comments [ctx comments]
  (when comments
    (let [comment-component (ui/component ctx :comment)]
      [:div.comments
       (map (fn [c]
              [:div {:key (:_id c)} 
               [comment-component c]]) comments)])))

(defn render-block [ctx current-idx comments idx block]
  (when (not (empty? block))
    (let [block-comments (get comments idx)]
      [:div.block.cf {:key idx}
       [:div.post-block {:dangerouslySetInnerHTML {:__html block}}]
       [:div.comments-block
        [:button.open-comment-form
         {:class (when (= current-idx idx) "open-comment-form--active")
          :on-click #(ui/send-command ctx :open-comment-form idx)}
         [:i.fa.fa-plus]]
        [:div {:class (when (pos? (count block-comments)) "with-comments")}
         (when (= current-idx idx)
           [(ui/component ctx :comment-form)])]
        (render-comments ctx block-comments)]])))

(defn render [ctx]
  (fn []
    (let [current-post-sub (ui/subscription ctx :current-post)
          current-post @current-post-sub
          current-comment-form-idx-sub (ui/subscription ctx :current-comment-form-idx)
          current-comment-form-idx @current-comment-form-idx-sub
          comments-sub (ui/subscription ctx :comments)
          grouped-comments (group-comments @comments-sub)
          ]
      [:div.feedback-component 
       (map-indexed (partial render-block ctx current-comment-form-idx grouped-comments)
                    (:parts current-post))])))

(def component (ui/constructor
                {:renderer render
                 :topic :feedback
                 :component-deps [:comment-form
                                  :comment]
                 :subscription-deps [:current-post
                                     :comments
                                     :current-comment-form-idx]}))
