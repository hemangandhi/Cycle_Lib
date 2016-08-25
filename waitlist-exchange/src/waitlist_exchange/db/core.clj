(ns waitlist-exchange.db.core
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojure.java.io :as io]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [clojurewerkz.neocons.rest.labels :as lbls]
            [clojurewerkz.neocons.rest.nodes :as nn]))

(def neo4j-conn (nr/connect (str "http://neo4j:" 
                                 (.trim (slurp (io/resource "neo4j-pass.txt")))
                                 "@localhost:7474/db/data")))

(defn alter-keys [hashmap alterer]
  (zipmap (map alterer (keys hashmap)) (vals hashmap)))

(defn internalize [key-prefix hashmap]
  (alter-keys hashmap #(keyword (.replace % key-prefix ""))))

(defn users [email]
  (let [res (cy/tquery neo4j-conn (str "MATCH (val:User) WHERE val.email = \"" email "\" RETURN val.email, val.name, val.password"))]
    (if (empty? res) nil (internalize "val." (first res)))))

(defn add-user [name email pass]
  (if (not (nil? (users email)))
    nil
    (let [node (nn/create neo4j-conn {:name name :email email :password pass})]
      (lbls/add neo4j-conn node "User")
      (:data node))))
