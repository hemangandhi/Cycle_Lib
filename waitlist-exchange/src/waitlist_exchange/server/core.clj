(ns waitlist-exchange.server.core
  (:require [cemerick.friend :as friend]
            [cemerick.friend.workflows :refer (make-auth)]
            [cemerick.friend.credentials :as creds]
            [compojure.core :refer (GET POST routes defroutes)]
            [compojure.handler :refer (site)]
            [ring.util.response :as resp]
            [ring.adapter.jetty :refer [run-jetty]]
            [waitlist-exchange.html.core :as wle-html]))
      
(defroutes main-app
  (GET "/" req (wle-html/gen-page false ""))
  (GET "/utest" req (wle-html/gen-page true "Tester")))

(defn start-srv []
  (run-jetty main-app {:port 8080}))
