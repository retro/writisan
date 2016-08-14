(ns writisan-client.util.pipeline)

(defmacro p-> [args & steps]
  (into [] (map (fn [s]
                  `(fn ~args ~s)) steps)))
