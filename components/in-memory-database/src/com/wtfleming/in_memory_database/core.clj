(ns com.wtfleming.in-memory-database.core
  (:require [com.stuartsierra.component :as component]))

;; TODO copy/paste this into README/docs to explain how database is represented
;;    and leave in this src file as a comment
(def foo-db
  {:indicators {1 {:indicator "85.93.20.243"
                   :description ""
                   :created "2018-07-09T18:02:40"
                   :title ""
                   :content ""
                   :type "IPv4"
                   :author-name "scottlsattler"
                   :id 1}
                2 {:indicator "71.24.15.164"
                   :description ""
                   :created "2019-07-10T18:02:40"
                   :title ""
                   :content ""
                   :type "YARA"
                   :author-name "scottlsattler"
                   :id 2}
                3 {:indicator "91.92.33.129"
                   :description ""
                   :created "2020-07-11T18:02:40"
                   :title ""
                   :content ""
                   :type "IPv4"
                   :author-name "AlienVault"
                   :id 3}}
   ;; Map representing an inverted index of indicators by type
   :indicator-type->ids {"YARA" #{2}
                         "IPv4" #{1 3}}})

;; In Memory database implementation
(defrecord InMemoryDatabase [data]
  component/Lifecycle
  (start [this]
    (println "STARTING in memory database")
    ;; TODO the component should be idempotent?

    ;; TODO load data from disk here instead of using foo-db
    ;;   make a load-db-from-file fn so we can unit/integration test it
    (assoc this :data foo-db))

  (stop [this]
    (println "STOPPING in memory database")
    this))

(defn create []
  (component/using (map->InMemoryDatabase {}) []))

(defn get-indicator-by-id
  "Given an id, returns the indicator."
  [db-component id]
  (let [indicators (get-in db-component [:data :indicators])]
    (get indicators id)))

(defn get-all-indicators
  "Returns all indicators"
  [db-component]
  (let [indicators (get-in db-component [:data :indicators])]
    (if-let [values (vals indicators)]
      values
      [])))

(defn get-all-indicators-by-type
  "Given a type, returns all indicators of that type"
  [db-component type]
  (let [indicators (get-in db-component [:data :indicators])
        indicator-type->ids (-> (get-in db-component [:data :indicator-type->ids]))
        ids (get indicator-type->ids type)]
    ;; Given a vector of indicator ids,
    ;; lookup and return the actual indicators from the indicators map
    (mapv #(get indicators %) ids)))
