(ns writisan-client.util
  (:require [garden.core :as garden]
            [clojure.string :refer [join trim]]
            [goog.crypt :refer [byteArrayToHex]])
  (:import goog.crypt.Md5))

(defn class-names [checks]
  (join " " (filter (complement nil?)
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
    (.update md5 (trim email))
    (str "//www.gravatar.com/avatar/" (byteArrayToHex (.digest md5)) "?size=200")))
