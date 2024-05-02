(ns com.wtfleming.indicator.interface
  (:require [com.wtfleming.indicator.core :as core]))

(defn get-indicator-by-id [db id]
  (core/get-indicator-by-id db id))

(defn get-all-indicators [db]
  (core/get-all-indicators db))

(defn get-all-indicators-by-type [db type]
  (core/get-all-indicators-by-type db type))
