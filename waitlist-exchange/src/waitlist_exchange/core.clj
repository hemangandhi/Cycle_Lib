(ns waitlist-exchange.core
  (:require [waitlist-exchange.server.core :refer [start-srv]]))

(defn -main [& args]
  (start-srv))
