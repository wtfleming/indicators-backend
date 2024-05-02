(ns com.wtfleming.in-memory-database.core
  (:require [com.stuartsierra.component :as component]))

;; TODO copy/paste this into README/docs to explain how database is represented
;;    and leave in this src file as a comment
(def foo-db
  {:indicators [{:indicator "85.93.20.243"
                 :description ""
                 :created "2018-07-09T18:02:40"
                 :title ""
                 :content ""
                 :type "IPv4"
                 :id 1}
                {:indicator "71.24.15.164"
                 :description ""
                 :created "2019-07-10T18:02:40"
                 :title ""
                 :content ""
                 :type "YARA"
                 :id 2}
                {:indicator "91.92.33.129"
                 :description ""
                 :created "2020-07-11T18:02:40"
                 :title ""
                 :content ""
                 :type "IPv4"
                 :id 3}]
   ;; Map for doing lookups by id
   :indicator-idx-by-id {1 0
                         2 1
                         3 2}
   ;; Map representing an inverted index of indicators by type
   :indicator-idxs-by-type {"YARA" [1]
                            "IPv4" [0 2]}})

;; In Memory database implementation
(defrecord InMemoryDatabase [data]
  component/Lifecycle
  (start [this]
    (println "STARTING in memory database")
    ;; TODO the component should be idempotent?

    ;; TODO load data from disk here instead of using foo-db
    (assoc this :data foo-db))

  (stop [this]
    (println "STOPPING in memory database")
    this))

(defn create []
  (component/using (map->InMemoryDatabase {}) []))

;; FIXME write tests for these fns using a foo-db like fixture

(defn get-indicator-by-id
  "Given an id, returns the indicator."
  [db-component id]
  (let [indicators (get-in db-component [:data :indicators])
        idx (-> (get-in db-component [:data :indicator-idx-by-id])
                (get id))]
    (nth indicators idx)))

(defn get-all-indicators
  "Returns all indicators"
  [db-component]
  (get-in db-component [:data :indicators]))

(defn get-all-indicators-by-type
  "Given a type, returns all indicators of that type"
  [db-component type]
  (let [indicators (get-in db-component [:data :indicators])
        idxs (-> (get-in db-component [:data :indicator-idxs-by-type])
                 (get type))]
    ;; Given a vector of indices into the indicators vector,
    ;; lookup and return the actual indicators
    (mapv #(nth indicators %) idxs)))
