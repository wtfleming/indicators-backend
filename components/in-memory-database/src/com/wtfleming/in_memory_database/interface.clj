(ns com.wtfleming.in-memory-database.interface
  (:require [com.wtfleming.in-memory-database.core :as core]))

(defn create []
  (core/create))

(defn get-indicator-by-id [db id]
  (core/get-indicator-by-id db id))

(defn get-all-indicators [db]
  (core/get-all-indicators db))

(defn get-all-indicators-by-type [db type]
  (core/get-all-indicators-by-type db type))
