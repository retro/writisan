(ns writisan-client.components.editor
  (:require [keechma.ui-component :as ui]
            [reagent.core :as reagent]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [writisan-client.stylesheets.core :refer-macros [defelement]]
            [writisan-client.components.spinner :refer [spinner]]
            [writisan-client.components.codemirror :as codemirror]
            [writisan-client.util :refer [markdown-word-counter with-inner-html]]))

(def css-transition-group
  (reagent/adapt-react-class js/React.addons.CSSTransitionGroup))

(def error-transition
  (let [timeout 300
        height "42px"]
    {:timeout timeout
     :height height
     :hide {:opacity 0.01
            :overflow-y "hidden"
            :max-height 0}
     :show {:max-height height
            :opacity 1
            :transition (str "all " timeout "ms cubic-bezier(0, 1, 0.5, 1)")}}))

(defelement error-transition-wrap
  :style [{:height (:height error-transition)}
          [:&.error-enter (:hide error-transition)]
          [:&.error-enter.error-enter-active (:show error-transition)]
          [:&.error-leave (:show error-transition)]
          [:&.error-leave.error-leave-active (:hide error-transition)]]
  :class [:mxn2])

(defelement outer-codemirror-wrap
  :style {:padding-top "85px"
          :box-shadow "0 18px 16px -16px rgba(0, 0, 0, 0.1)"})

(defelement codemirror-wrap
  :class [:bg-white :px3 :py2])

(defelement document-header-container
  :class [:bd-white :bg-white-d :border :bw2 :clearfix :container :px2 :py2])

(defelement document-header
  :class [:fixed :right-0 :left-0 :z1]
  :style {:border-top (str "20px solid " (:silver-l colors-with-variations))})

(defelement save-button
  :class [:bg-belizehole :bg-h-belizehole-d :border-none :btn :c-white :h4 :line-height-4 :pill :px3 :relative :right]
  :style {:margin-right "-3px"}
  :tag :button)

(defelement spinner-wrap
  :class [:absolute]
  :style {:margin-left "-25px"
          :margin-top "2px"})

(defelement word-count
  :class [:c-black-l :h5 :left :monospaced]
  :style {:padding-top "7px"})

(defelement save-notice
  :class [:c-silver-d :center :h5 :p2])

(defelement error-message
  :class [:bg-pomegranate :c-white :px2 :py1 :relative :rounded])

(defelement close-icon
  :class [:absolute :bottom-0 :c-white :center :cursor-pointer :h2 :right-0 :top-0]
  :style [{:opacity "0.3"
           :width "34px"
           :padding-top "1px"}
          [:&:hover {:opacity 1}]])

(defn render-error-transition [& children]
  [css-transition-group {:component "div"
                         :transition-name "error"
                         :transition-enter-timeout (:timeout error-transition)
                         :transition-leave-timeout (:timeout error-transition)}
   children])

(defn render-error-message [doc clear-error]
  (let [error (get-in doc [:meta :error])]
    (render-error-transition
     (when error
       [-error-transition-wrap {:key "error-transition"}
        [-error-message error
         [-close-icon (with-inner-html {:on-click clear-error} "&times;")]]]))))

(defn render-count-info [count-info]
  (str (:chars count-info) " characters &bullet; " (:words count-info) " words"))

(defn render [ctx]
  (let [content (reagent/atom "")
        clear-error #(ui/send-command ctx :clear-error)]
    (fn []
      (let [count-info (markdown-word-counter @content)
            is-saving-document? @(ui/subscription ctx :is-saving-document?)
            current-document @(ui/subscription ctx :current-document)]
        [:div
         [-document-header
          [-document-header-container
           [-word-count (with-inner-html (render-count-info count-info))]
           [-save-button {:on-click #(ui/send-command ctx :save @content)}
            (when is-saving-document? [-spinner-wrap [spinner 20 "#fff"]])
            "Save"]]]
         [-outer-codemirror-wrap
          [-codemirror-wrap
           [render-error-message current-document clear-error]
           [codemirror/render clear-error content]]]
         [-save-notice "After you save the document, you'll be able to share it from the next screen"]]))))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:is-saving-document? :current-document]
                   :topic :editor}))
