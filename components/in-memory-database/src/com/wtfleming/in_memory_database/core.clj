(ns com.wtfleming.in-memory-database.core
  (:require [com.stuartsierra.component :as component]))

;; In Memory database implementation
(defrecord InMemoryDatabase [db]
  component/Lifecycle
  (start [this]
    (println "STARTING in memory database")

      ;; TODO the component should be idempotent?
    (assoc this :db {:foo "TODO IMPLEMENT DB"}))

  (stop [this]
    (println "STOPPING in memory database")
    this))

(defn create []
  ;; TODO load database from disk here
  ;; FIXME need to create the record with map->InMemoryDatabase? since nothing in map?
  (component/using (map->InMemoryDatabase {}) []))

(defn get-indicator-by-id [db id]
  {:id id :foo "bar"})

(defn get-all-indicators [db]
  [{:id 1 :foo "bar"} {:id 2 :foo "baz"}])

(defn get-all-indicators-by-type [db type]
  [{:id 1 :type type}])

