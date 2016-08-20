(ns writisan-client.util
  (:require [garden.core :as garden]
            [clojure.string :as str]
            [cljsjs.markdown-it]
            [goog.crypt :refer [byteArrayToHex]])
  (:import goog.crypt.Md5))

(defn with-inner-html
  ([html] (with-inner-html {} html))
  ([props html]
   (assoc props :dangerouslySetInnerHTML {:__html html})))

(def markdown-word-counter
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

(defn class-names [checks]
  (str/join " " (filter (complement nil?)
                    (map (fn [[k v]]
                           (when (if (fn? v) (v) v) (name k))) checks))))

(defn generate-and-inject-style-tag
  "Injects a style tag with the id 'injected-css' into the page's head tag
   Returns generated style tag"
  []
  (let [ page-head (.-head js/document)
         style-tag (.createElement js/document "style")]    
       (.setAttribute style-tag "id" "injected-css")
       (.appendChild page-head style-tag)))

(defn update-page-css
  "Updates #injected-css with provided argument (should be some CSS string 
   -- e.g. output from garden's css fn) If page does not have #injected-css then
   will create it via call to generate-and-inject-style-tag"
  [stylesheet]
  (let [ style-tag-selector "#injected-css"
         style-tag-query (.querySelector js/document style-tag-selector)
         style-tag (if (nil? style-tag-query)
                       (generate-and-inject-style-tag) 
                       style-tag-query)]
       (aset style-tag "innerHTML" (garden/css stylesheet))))

(defn gravatar-url [email]
  (let [md5 (Md5.)]
    (.update md5 (str/trim email))
    (str "//www.gravatar.com/avatar/" (byteArrayToHex (.digest md5)) "?size=200")))
