(ns writisan-client.components.editor
  (:require [keechma.ui-component :as ui]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.markdown]
            [cljsjs.codemirror.addon.display.placeholder]
            [reagent.core :as reagent]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [cljsjs.markdown-it]
            [clojure.string :refer [split trim]])
  (:require-macros [writisan-client.util :refer [$$ defelement]]))

(def md (.markdownit js/window))

(defn word-count [markdown]
  (if (empty? (trim markdown))
    {:chars 0 :words 0}
    (let [res (.render md markdown)
          div (.createElement js/document "div")]
      (aset div "innerHTML" res)
      (let [inner-text (.-innerText div)]
        {:chars (count inner-text)
         :words (count (split inner-text " "))}))))

(defelement codemirror-wrap
  :style {:border-top (str "1px solid " (:silver-l colors-with-variations))} 
  :class [:z1]
  :tag :ul)


(defn stylesheet []
  [[:.editor {}]
   [:.editor--codemirror-wrap {}]
   [:.editor--button-wrap {:border-top (str "20px solid " (:silver-l colors-with-variations))}]
   [:.editor--outer-codemirror-wrap {:padding-top "85px"
                                     :box-shadow "0 18px 16px -16px rgba(0, 0, 0, 0.1)"}]
   [:.editor--word-count {:padding-top "7px"}]])

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
      (let [count-info (word-count @content)]
        ($$
         [:div.editor
          [:$button-wrap
           [:$button-container
            [:div.left.editor--word-count.h5.c-black-l.monospaced (str (:chars count-info) " characters / " (:words count-info) " words")]
            [:$save-button {:on-click #(ui/send-command ctx :save-article @content)} "Save"]]]
          [:$outer-codemirror-wrap
           [:$codemirror-wrap
            [codemirror-editor content]]]
          [:div.center.h5.p2.c-silver-d "After you save the document, you'll be able to share it from the next screen"]]

         {:$button-container [:div.container.bg-white-d.px2.py2.clearfix.border.bd-white.bw2]
          :$outer-codemirror-wrap [:div.editor--outer-codemirror-wrap]
          :$codemirror-wrap [:div.bg-white.px3.py2.editor--codemirror-wrap]
          :$button-wrap [:div.fixed.right-0.left-0.z1.editor--button-wrap]
          :$save-button [:button.btn.line-height-4.px3.h4.right
                         :.bg-belizehole.bg-h-belizehole-d.c-white
                         :.border-none.pill]})))))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:is-saving-article?]
                   :topic :editor}))
