(ns writisan.components.session
  (:require [keechma.ui-component :as ui]))

(defn render-login
  "Renders the login screen"
  [ctx]
  [:button.btn.btn-primary
    {:on-click #(ui/send-command ctx :login)}
    "Login"])

(defn render-main-app
  [main-app]
  [(:main-component main-app)])

(defn render [ctx]
  (fn []
    (let [main-app-sub (ui/subscription ctx :main-app)
          login-status-sub (ui/subscription ctx :login-status)
          main-app @main-app-sub
          login-status @login-status-sub]
      [:div.container>div.row>div.col-xs-12>div.jumbotron
       (case login-status
         :loading [:div "LOADING"]
         (if main-app
           (render-main-app main-app)
           (render-login ctx)))])))

(def component (ui/constructor
                {:renderer render
                 :subscription-deps [:main-app :login-status]
                 :topic :login}))
