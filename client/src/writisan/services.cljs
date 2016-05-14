(ns writisan.services)

(defn service [name]
  (.service js/F name))

(def posts-service (service "posts"))
(def comments-service (service "comments"))
(def post-users-service (service "post-users"))

(defn success-processor [success-cb data]
  (let [converted (js->clj data :keywordize-keys true)]
    (.log js/console converted)
    (success-cb converted)))

(defn error-cb [e]
  (.error js/console e))

(defn create-item [service data success-cb]
 (.then
   (.create service data)
   (partial success-processor success-cb)
   error-cb))

(defn get-item [service id success-cb]
  (.then
   (.get service id)
   (partial success-processor success-cb)
   error-cb))

(defn find-items [service params success-cb]
  (.log js/console params)
  (.then
   (.find service #js{:query params})
   (partial success-processor success-cb)
   error-cb))
