(ns writisan-client.components.editor
  (:require [keechma.ui-component :as ui]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.markdown]
            [cljsjs.codemirror.addon.display.placeholder]
            [reagent.core :as reagent]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [cljsjs.markdown-it]
            [clojure.string :as str :refer [split trim]]
            [writisan-client.stylesheets.core :refer-macros [defelement]]
            [writisan-client.components.spinner :refer [spinner]]))

(def css-transition-group
  (reagent/adapt-react-class js/React.addons.CSSTransitionGroup))

(defelement outer-codemirror-wrap
  :style {:padding-top "85px"
          :box-shadow "0 18px 16px -16px rgba(0, 0, 0, 0.1)"})

(defelement codemirror-wrap
  :class [:bg-white :px3 :py2])

(defelement document-header-container
  :class [:bg-white-d :px2 :py2 :clearfix :border :bd-white :bw2 :container])

(defelement document-header
  :class [:fixed :right-0 :left-0 :z1]
  :style {:border-top (str "20px solid " (:silver-l colors-with-variations))})

(defelement save-button
  :class [:btn :line-height-4 :px3 :h4 :right :bg-belizehole :bg-h-belizehole-d :c-white :border-none :pill :relative]
  :tag :button)

(defelement spinner-wrap
  :class [:absolute]
  :style {:margin-left "-25px"
          :margin-top "2px"})

(defelement word-count
  :class [:left :h5 :c-black-l :monospaced]
  :style {:padding-top "7px"})

(defelement save-notice
  :class [:center.h5.p2.c-silver-d])

(def error-timeout 300)
(def error-hidden {:opacity 0.01
                   :overflow-y "hidden"
                   :max-height 0})
(def error-shown {:max-height "42px"
                  :opacity 1
                  :transition (str "all " error-timeout "ms cubic-bezier(0, 1, 0.5, 1)")})

(defelement error-transition-wrap
  :style [{:max-height "42px"
           :height "42px"}
          [:&.error-enter error-hidden]
          [:&.error-enter.error-enter-active error-shown]
          [:&.error-leave error-shown]
          [:&.error-leave.error-leave-active error-hidden]]
  :class [:mxn2])

(defelement error-message
  :class [:.bg-pomegranate.rounded.c-white.px2.py1.relative])

(defelement close-icon
  :class [:.c-white.cursor-pointer.right-0.bottom-0.top-0.center.h2.absolute]
  :style [{:opacity "0.5"
            :width "34px"
            :padding-top "1px"}
          [:&:hover {:opacity 1}]])

(def word-counter
  ((fn []
     (let [md (.markdownit js/window)
           div (.createElement js/document "div")]
       (fn [markdown]
         (if (empty? (str/trim markdown))
           {:chars 0 :words 0}
           (let [res (.render md markdown)
                 div (.createElement js/document "div")] 
             (aset div "innerHTML" res)
             (let [inner-text  (str/replace (str/trim (.-innerText div)) #"\s+" " ")]
               {:chars (count (str/replace inner-text " " ""))
                :words (count (str/split inner-text #"\s+"))}))))))))

(defn mount-codemirror [clear-error content c]
  (let [dom-node (reagent/dom-node c)
        cm (js/CodeMirror dom-node #js{:value @content
                                       :theme "mirrormark"
                                       :mode "markdown"
                                       :autofocus true
                                       :viewportMargin js/Infinity
                                       :placeholder "Text goes here..."
                                       :lineWrapping true})]
    (.on cm "change" (fn [cm]
                       (clear-error)
                       (reset! content (.getValue cm))))))

(defn codemirror-editor [clear-error content]
  (reagent/create-class
   {:component-did-mount (partial mount-codemirror clear-error content) 
    :reagent-render (fn [] [:div {:key "codemirror"}])}))

(defn render-error-message [doc clear-error]
  (let [error (get-in doc [:meta :error])]
    [css-transition-group {:transition-name "error"
                           :transition-enter-timeout error-timeout
                           :transition-leave-timeout error-timeout}
     (when error
       [-error-transition-wrap
        [-error-message {:key "error-msg"}
         error
         [-close-icon {:on-click clear-error
                       :dangerouslySetInnerHTML {:__html "&times;"}}]]])]))

(defn render [ctx]
  (let [content (reagent/atom "")
        clear-error #(ui/send-command ctx :clear-error)]
    (fn []
      (let [count-info (word-counter @content)
            is-saving-document? @(ui/subscription ctx :is-saving-document?)
            current-document @(ui/subscription ctx :current-document)]
        [:div
         [-document-header
          [-document-header-container
           [-word-count (str (:chars count-info) " characters / " (:words count-info) " words")]
           [-save-button {:on-click #(ui/send-command ctx :save @content)}
            (when is-saving-document? [-spinner-wrap [spinner 20 "#fff"]])
            "Save"]]]
         [-outer-codemirror-wrap
          [-codemirror-wrap
           
           [render-error-message current-document clear-error]
           [codemirror-editor clear-error content]]]
         [-save-notice "After you save the document, you'll be able to share it from the next screen"]]))))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:is-saving-document? :current-document]
                   :topic :editor}))
