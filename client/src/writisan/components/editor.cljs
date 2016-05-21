(ns writisan.components.editor
  (:require [keechma.ui-component :as ui]
            [cljsjs.codemirror]
            [cljsjs.codemirror.mode.markdown]
            [cljsjs.codemirror.addon.display.placeholder]
            [reagent.core :as reagent]))


(defn mount-codemirror [content c]
  (let [dom-node (.getDOMNode c)
        cm (js/CodeMirror dom-node #js{:value @content
                                       :theme "mirrormark"
                                       :mode "markdown"
                                       :autofocus true
                                       :viewportMargin js/Infinity
                                       :placeholder "Text goes here..."
                                       :lineWrapping true})]
    (.on cm "change" (fn [cm] (reset! content (.getValue cm))))))

(defn codemirror-editor [content]
  (reagent/create-class
   {:component-did-mount (partial mount-codemirror content) 
    :reagent-render (fn []
                      [:div])}))

(defn render [ctx]
  (let [content (atom "")
        is-saving-article-sub? (ui/subscription ctx :is-saving-article?)]
    (fn []
      [:div.editor-component
       [:div.toolbar>div.content
        [:i.fa.fa-arrow-down]
        " Enter your document and you'll be able to share it from the next screen"
        [:button.btn {:on-click #(ui/send-command ctx :save-article @content)} "Save"]]
       [codemirror-editor content]
       (when @is-saving-article-sub?
         [:div.saving-article-overlay
          [:div
           [:i.fa.fa-circle-o-notch.fa-spin]]])])))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:is-saving-article?]
                   :topic :editor}))
