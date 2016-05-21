(ns writisan.components.session
  (:require [keechma.ui-component :as ui]))

(defn render-login
  "Renders the login screen"
  [ctx]
  [:div.login-screen
   [:h1 "Welcome to Writisan."]
   [:p "Login with your Google account to start"]
   [:button
    {:on-click #(ui/send-command ctx :login)}
    "Login"]])

(defn render-main-app
  [main-app]
  [(:main-component main-app)])

(defn render [ctx]
  (fn []
    (let [main-app-sub (ui/subscription ctx :main-app)
          login-status-sub (ui/subscription ctx :login-status)
          main-app @main-app-sub
          login-status (or @login-status-sub :loading)]
      (case login-status
        :loading [:div.loading>i.fa.fa-circle-o-notch.fa-spin]
        :anon (render-login ctx)
        :auth (render-main-app main-app)))))

(def component (ui/constructor
                {:renderer render
                 :subscription-deps [:main-app :login-status]
                 :topic :login}))
