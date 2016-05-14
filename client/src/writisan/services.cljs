(ns writisan.services)

(defn service [name]
  (.service js/F name))

(def posts-service (service "posts"))

(defn success-one [success-cb item]
  (let [converted (js->clj item :keywordize-keys true)]
    (success-cb converted)))

(defn error-cb [e]
  (.error js/console e))

(defn create-item [service data success-cb]
 (.then
   (.create service data)
   (partial success-one success-cb)
   error-cb))

(defn get-item [service id success-cb]
  (.then (.get service id))
  (partial success-one success-cb)
  error-cb)
