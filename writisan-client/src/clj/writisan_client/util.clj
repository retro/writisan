(ns writisan-client.util
  (:require [clojure.walk :refer [postwalk-replace]]))

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
    `(do
       (writisan-client.stylesheets.core/register-component-styles ~(apply hash-map args))
       (def ~name ~(apply hash-map args)))))
