(ns waitlist-exchange.server.core
  (:require [cemerick.friend :as friend]
            [waitlist-exchange.db.user-db :as wle-udb]
            [cemerick.friend.workflows :refer (make-auth)]
            [cemerick.friend.credentials :as creds]
            [compojure.core :refer (GET POST routes defroutes)]
            [compojure.handler :refer (site)]
            [ring.util.response :as resp]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [waitlist-exchange.html.core :as wle-html]))
      
(defroutes main-app
  (GET "/" req (wle-html/gen-page false ""))
  (GET "/utest" req (friend/authorize #{::user} 
                                      (wle-html/gen-page true 
                                                         (get-in (friend/current-authentication req) 
                                                                 [:user :name]))))
  (friend/logout (GET "/logout" [] (wle-html/gen-page false ""))))

(defn wle-cred-fn [{:keys [email password] :as creds}]
  (when-let [usr (@wle-udb/users email)]
    (when (= (creds/hash-bcrypt password) (usr :password))
      {:identity email :roles #{::user} :user usr})))

(defn wle-auth-wkflow [req]
  (when (and (= (:request-method req) :post)
             (= (:uri req) "/login"))
    (let [cred-fn (get-in req [::friend/auth-config :credential-fn])]
      (make-auth (cred-fn (select-keys (:params req) [:email :password]))))))

(defn start-srv []
  (run-jetty (-> main-app
                 (friend/authenticate {:workflows [wle-auth-wkflow]
                                       :credential-fn wle-cred-fn})
                 (wrap-keyword-params)
                 (wrap-params)
                 (wrap-session)) {:port 8080}))
