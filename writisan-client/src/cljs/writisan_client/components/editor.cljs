(ns writisan-client.components.editor
  (:require [keechma.ui-component :as ui]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.markdown]
            [cljsjs.codemirror.addon.display.placeholder]
            [reagent.core :as reagent]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [cljsjs.markdown-it]
            [clojure.string :refer [split trim]]
            [writisan-client.stylesheets.core :refer-macros [defelement]]))

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
  :class [:btn :line-height-4 :px3 :h4 :right :bg-belizehole :bg-h-belizehole-d :c-white :border-none :pill]
  :element :button)

(defelement word-count
  :class [:left :h5 :c-black-l :monospaced]
  :style {:padding-top "7px"})

(defelement save-notice
  :class [:center.h5.p2.c-silver-d])

(def word-counter
  ((fn []
     (let [md (.markdownit js/window)
           div (.createElement js/document "div")]
       (fn [markdown]
         (if (empty? (trim markdown))
           {:chars 0 :words 0}
           (let [res (.render md markdown)
                 div (.createElement js/document "div")] 
             (aset div "innerHTML" res)
             (let [inner-text (.-innerText div)]
               {:chars (count inner-text)
                :words (count (split inner-text #"\s+"))}))))))))

(defn mount-codemirror [content c] 
  (let [dom-node (reagent/dom-node c)
        cm (js/CodeMirror dom-node #js{:value @content
                                       :theme "mirrormark"
                                       :mode "markdown"
                                       :autofocus true
                                       :viewportMargin js/Infinity
                                       :placeholder "Text goes here..."
                                       :lineWrapping true})]
    (.on cm "change" (fn [cm]
                       (reset! content (.getValue cm))))))

(defn codemirror-editor [content]
  (reagent/create-class
   {:component-did-mount (partial mount-codemirror content) 
    :reagent-render (fn [] [:div])})) 

(defn render [ctx]
  (let [content (reagent/atom "")
        is-saving-article-sub? (ui/subscription ctx :is-saving-article?)]
    (fn []
      (let [count-info (word-counter @content)]
        [:div
         [-document-header
          [-document-header-container
           [-word-count (str (:chars count-info) " characters / " (:words count-info) " words")]
           [-save-button {:on-click #(ui/send-command ctx :save-article @content)} "Save"]]]
         [-outer-codemirror-wrap
          [-codemirror-wrap
           [codemirror-editor content]]]
         [-save-notice "After you save the document, you'll be able to share it from the next screen"]]))))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:is-saving-article?]
                   :topic :editor}))
