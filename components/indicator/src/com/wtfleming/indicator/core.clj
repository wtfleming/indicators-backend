(ns com.wtfleming.indicator.core
  (:require [com.wtfleming.in-memory-database.interface :as db]))


(defn get-indicator-by-id [db id]
  ;; TODO return the map and have the API convert it to json
  (str (db/get-indicator-by-id db id)))

(defn get-all-indicators [db]
  ;; TODO return the vector and have the API convert it to json
  (str (db/get-all-indicators db)))

(defn get-all-indicators-by-type [db type]
  ;; TODO return the vector and have the API convert it to json
  (str (db/get-all-indicators-by-type db type)))


