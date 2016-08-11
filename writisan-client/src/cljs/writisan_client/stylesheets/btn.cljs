(ns writisan-client.stylesheets.btn)

(defn stylesheet []
  [:.btn {:cursor 'pointer
          :-webkit-appearance 'none}
   [:&:focus {:outline 'none}]
   [:&:active {:outline 'none
               :box-shadow "inset 0 3px 0px 0 rgba(0,0,0,0.125),0 0 0 3px rgba(0,0,0,0.125)"}]])
