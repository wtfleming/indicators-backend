(ns com.wtfleming.indicator.interface
  (:require [com.wtfleming.indicator.core :as core]))

(defn get-indicator-by-id [id]
  (core/get-indicator-by-id id))

(defn get-all-indicators []
  (core/get-all-indicators))

(defn get-all-indicators-by-type [type]
  (core/get-all-indicators-by-type type))
