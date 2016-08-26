(ns waitlist-exchange.db.core
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojure.java.io :as io]
            [clojure.string :as clj-str]
            [clojurewerkz.neocons.rest.relationships :as nrl]
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

(defn fudge-id-attr [raw-node-info]
  (assoc raw-node-info :id (get-in raw-node-info [:metadata :id])))

(defn users-raw [email]
  (let [res (cy/query neo4j-conn (str "MATCH (val:User) WHERE val.email = \"" email "\" RETURN val"))]
    (if (cy/empty? res) nil res)))

(defn users [email]
  (let [raw (users-raw email)]
    (if (nil? raw) nil
      (get-in raw [:data 0 0 :data]))))

(defn add-user [name email pass]
  (if (not (nil? (users email)))
    nil
    (let [node (nn/create neo4j-conn {:name name :email email :password pass})]
      (lbls/add neo4j-conn node "User")
      (:data node))))

(defn get-or-make-desire [have-name have-ind want-name want-ind]
  (let [node-obj {:haveName have-name :haveInd have-ind :wantName want-name :wantInd want-ind}
        res (cy/query neo4j-conn (str "MATCH (v:Desire) WHERE " 
                                      (clj-str/join ", " (map (fn [[k v]] (str "v." k " = {" v "}")) node-obj))
                                      " RETURN v") node-obj)]
    (if (cy/empty? res) 
      (let [new-node (nn/create neo4j-conn node-obj)]
        (lbls/add neo4j-conn new-node "Desire")
        new-node)
      (ffirst (:data res)))))

(defn add-user-desire [email [have-name have-ind want-name want-ind :as desire-info]]
  (let [user-node (fudge-id-attr (users-raw email))
        desire-node-tmp (apply get-or-make-desire desire-info)
        desire-node (if (contains? desire-node-tmp :id) desire-node-tmp (fudge-id-attr desire-node-tmp))]
    (nrl/create user-node desire-node :Desires)))
