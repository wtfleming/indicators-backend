(ns com.wtfleming.in-memory-database.core)


(defn get-indicator-by-id [db id]
  {:id id :foo "bar"})

(defn get-all-indicators [db]
  [{:id 1 :foo "bar"} {:id 2 :foo "baz"}])

(defn get-all-indicators-by-type [db type]
  [{:id 1 :type type}])
