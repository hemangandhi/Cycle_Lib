(ns waitlist-exchange.db.user-db
  (:require [cemerick.friend.credentials :refer (hash-bcrypt)]
            [waitlist-exchange.db.core :refer (load-users write-user-info)]
            [clojure.set :refer (union)]))

(def users (add-watch (ref (load-users))
                      :db-update
                      (fn [key ref old new]
                        (write-user-info new)
                        new)))

(defn add-user [name email password]
  (if (contains? @users email)
    false
    (dosync (alter users assoc email 
                   {:name name
                    :password password
                    :haves {}
                    :wants {}})
            true)))

(defn add-pair [email pair]
  (if (or (contains? (get-in @users [email :haves])
                     (pair :want))
          (contains? (get-in @users [email :wants])
                     (pair :have)))
    false
    (dosync (alter users assoc-in [email :haves (pair :have)] pair)
            (alter users assoc-in [email :wants (pair :want)] pair)
            true)))


