(ns writisan-client.stylesheets.core
  (:require [garden-basscss.core :as core]
            [garden-basscss.vars :refer [vars]]
            [writisan-client.stylesheets.colors :as colors]
            [writisan-client.stylesheets.btn :as btn]
            [garden.core :as garden]
            [garden.units :refer [em]]
            [writisan-client.stylesheets.colors :refer [colors-with-variations]]
            [writisan-client.components.layout :as layout]
            [writisan-client.components.editor :as editor]))



(def system-font-stack
  "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif")

(def system-font-stack-monospace
  "'Menlo', 'Monaco', 'Consolas', 'Lucida Console', 'Lucida Sans Typewriter', 'Andale Mono', 'Courier New', monospaced")

(defn register-component-styles [styles]
  (.log js/console "REGISTERING COMPONENT STYLES" styles))

(swap! vars assoc-in [:typography :bold-font-weight] 700)
(swap! vars assoc-in [:typography :caps-letter-spacing] (em 0.1))

(defn stylesheet []
  [[:* {:box-sizing 'border-box}]
   [:body {:font-family system-font-stack
           :font-size "16px"
           :margin 0
           :background (:silver-l colors-with-variations)}]
   [:img {:max-width "100%"}]
   [:svg {:max-width "100%"}]
   (core/stylesheet)
   (btn/stylesheet)
   (colors/stylesheet)
   [:.container {:max-width "1200px"
                 :margin "0 auto"
                 :position "relative"}]
   [:.monospaced {:font-family system-font-stack-monospace}]
   [:.cursor-pointer {:cursor 'pointer}]
   [:.bw2 {:border-width "2px"}]
   [:.pill {:border-radius "999em"}]
   (layout/stylesheet)
   (editor/stylesheet)])

