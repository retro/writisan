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

