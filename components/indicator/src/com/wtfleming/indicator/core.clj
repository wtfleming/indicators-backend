(ns com.wtfleming.indicator.core)


(defn get-indicator-by-id [id]
  ;; TODO return the map and have the API convert it to json
  (str {:id id
        :foo "TODO"}))

(defn get-all-indicators []
  ;; TODO return the vector and have the API convert it to json
  (str [{:id "some-id"
         :foo "TODO"}]))

(defn get-all-indicators-by-type [type]
  ;; TODO return the vector and have the API convert it to json
  (str [{:id "some-id"
         :type type}]))


