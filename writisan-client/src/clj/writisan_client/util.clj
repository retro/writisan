(ns writisan-client.util
  (:require [clojure.walk :refer [postwalk-replace]]
            [clojure.string :refer [join]]))

(defn process-shortcuts [shortcuts]
  (reduce-kv (fn [m k v]
               (if (vector? v) 
                 (assoc m k (apply str (map name v)))
                 m)) shortcuts shortcuts))

(defmacro $$ [form shortcuts]
  (postwalk-replace (process-shortcuts shortcuts) form))

(defmacro defelement [name & args]
  (if (odd? (count args))
    (throw "Args must have even number of elements")
    (let [config (apply hash-map args)
          element (:element config) 
          el-class (str "." (gensym name))
          el-class-keyword (keyword el-class)
          classes (:class config)
          styles (or (:style config) {})]
      `(do
         (writisan-client.stylesheets.core/register-component-styles [~el-class-keyword ~styles])
         (def ~name (str (name (or '~element "div")) "." ~el-class "." (join "." (map name ~classes))))))))
