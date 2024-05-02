(ns com.wtfleming.indicator.core
  (:require [com.wtfleming.in-memory-database.interface :as db]))

(defn get-indicator-by-id [db id]
  (db/get-indicator-by-id db id))

(defn get-all-indicators [db]
  (db/get-all-indicators db))

(defn get-all-indicators-by-type [db type]
  (db/get-all-indicators-by-type db type))



