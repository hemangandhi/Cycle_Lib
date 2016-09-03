(ns waitlist-exchange.server.core
  (:require [cemerick.friend :as friend]
            [waitlist-exchange.db.core :as wle-udb]
            [cemerick.friend.workflows :refer (make-auth)]
            [cemerick.friend.credentials :as creds]
            [compojure.core :refer (GET POST routes defroutes)]
            [compojure.handler :refer (site)]
            [ring.util.response :as resp]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))
      
(defroutes main-app
  (GET "/" req (wle-html/gen-page false ""))
  (GET "/my-trades" req (friend/authorize #{::user} 
                                      (wle-html/gen-page true 
                                                         (get-in (friend/current-authentication req) 
                                                                 [:user :name]))))
  (GET "/bad-login" req (wle-html/gen-page false "" :body-fn wle-html/login-fail-body))
  (friend/logout (GET "/logout" req (wle-html/gen-page false "")))
  (GET "/mk-account" [] (wle-html/gen-page false "" :body-fn wle-html/make-acc-body))
  (POST "/submit-account" req (if-let [usr (apply wle-udb/add-user (let [[email name pass] 
                                                                        (vals (assoc-in 
                                                                                (:params req) 
                                                                                [:password]
                                                                                (creds/hash-bcrypt (:password 
                                                                                                   (:params req)))))]
                                                                     [name email pass]))]
                                (friend/merge-authentication (resp/redirect "/my-trades") 
                                                               {:identity (get-in req [:params :email]) 
                                                                :roles #{::user} 
                                                                :user usr})
                                (wle-html/gen-page false "" :body-fn #(wle-html/make-acc-body :more-info [:p "This user already exists!"])))))

(defn wle-cred-fn [{:keys [email password] :as c-creds}]
  (when-let [usr (wle-udb/users email)]
    (when (creds/bcrypt-verify password (str (usr :password)))
      {:identity email :roles #{::user} :user usr})))

(defn wle-auth-wkflow [req]
  (when (and (= (:request-method req) :post)
             (= (:uri req) "/login"))
    (let [cred-fn (get-in req [::friend/auth-config :credential-fn])]
      (if-let [auth (cred-fn (select-keys (:params req) [:email :password]))]
        (friend/merge-authentication (resp/redirect "/my-trades") auth)
        (resp/redirect "/bad-login")))))

(defn start-srv []
  (run-jetty (-> main-app
                 (friend/authenticate {:workflows [wle-auth-wkflow]
                                       :credential-fn wle-cred-fn})
                 (wrap-keyword-params)
                 (wrap-params)
                 (wrap-session)) {:port 8080}))
