(ns writisan-client.components.codemirror
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.markdown]
            [cljsjs.codemirror.addon.display.placeholder]
            [reagent.core :as reagent]))

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

(defn render [clear-error content]
  (reagent/create-class
   {:component-did-mount (partial mount-codemirror clear-error content) 
    :reagent-render (fn [] [:div {:key "codemirror"}])}))
