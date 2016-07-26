(ns waitlist-exchange.db.core
  (:import [java.io PushbackReader])
  (:require [clojure.java.io :as io]))

(defn read-from-file [filename]
  (with-open [rdr (PushbackReader. (io/reader filename))]
    (binding [*read-eval* false]
      (read rdr))))

(defn write-to-file [filename data]
  (spit filename (pr data)))

(def default-path (io/resource "db/"))

(defn write-user-info [info]
  (write-to-file (str default-path "user-db.edn")))

(defn load-users [] (read-from-file (str default-path "user-db.edn")))

(defn write-cycles [cycles]
  (write-to-file (str default-path "cycles.edn")))

(defn load-cycles [] (read-from-file (str default-path "cycles.edn")))
