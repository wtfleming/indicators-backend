(ns com.wtfleming.in-memory-database.interface
  (:require [com.wtfleming.in-memory-database.core :as core]))

(defn create
  "Create a component for use in a component system"
  [filepath]
  (core/create filepath))

(defn get-indicator-by-id
  "Given and id, returns a single indicator"
  [db id]
  (core/get-indicator-by-id db id))

(defn get-all-indicators
  "Returns all indicators"
  [db]
  (core/get-all-indicators db))

(defn get-all-indicators-by-type
  "Returns all indicators of a type"
  [db type]
  (core/get-all-indicators-by-type db type))
