(ns com.wtfleming.in-memory-database.core
  (:require [com.stuartsierra.component :as component]
            [jsonista.core :as json]))

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
                         "IPv4" #{1 3}}

   ;; Map representing an inverted index of indicators by author-name
   :indicator-author-name->ids {"scottlsattler" #{1 2}
                                "AlienVault" #{3}}})

(defn- newer-indicator?
  "Returns true if indicator-a was created after indicator-b, else false."
  [indicator-a indicator-b]
  ;; an indicator has a created string that looks like:
  ;; "2018-07-09T12:48:22.196000"
  ;; So we can compare the strings lexicographically with compare
  ;; to determine if it is was created after the other one
  (-> (compare (:created indicator-a) (:created indicator-b))
      pos?))

(defn- add-indicator-to-type-index
  "Indexes an indicator's type. Returns the updated index."
  [{:keys [id type] :as _indicator} {:keys [indicator-type->ids] :as _db}]
  ;; Note that an indicator's type is expected to be an invariant.
  ;; If that turned out to not be the case we'd need to also
  ;; implement removing the indicator id of old type from the index!
  (let [;; Find the indicator ids with this type
        indicator-type-ids-set (get indicator-type->ids type #{})

        ;; Add the id of the indicator to the set
        indicator-type-ids-set (conj indicator-type-ids-set id)]

    ;; Return the updated index
    (assoc indicator-type->ids type indicator-type-ids-set)))

(defn- add-indicator-to-author-name-index
  "Indexes an indicator's author-name. Returns the updated index."
  [{:keys [id author-name] :as _indicator} {:keys [indicator-author-name->ids] :as _db}]
  ;; Note that an indicator's author-name is expected to be an invariant.
  ;; If that turned out to not be the case we'd need to also
  ;; implement removing the indicator id of old author-name from the index!
  (let [;; Find the indicator ids with this author-name
        indicator-author-name-ids-set (get indicator-author-name->ids author-name #{})

        ;; Add the id of the indicator to the set
        indicator-author-name-ids-set (conj indicator-author-name-ids-set id)]

    ;; Return the updated index
    (assoc indicator-author-name->ids author-name indicator-author-name-ids-set)))

(defn- create-indicator
  "Adds an indicator to the database. Returns the updated database."
  [{:keys [indicators] :as db} {:keys [id] :as indicator}]
  (let [;; Add the indicator to the indicators lookup map
        updated-indicators (assoc indicators id indicator)

        ;; Update the indicator type inverted index
        updated-types-index (add-indicator-to-type-index indicator db)

        ;; Update the indicator author-name inverted index
        updated-author-name-index (add-indicator-to-author-name-index indicator db)]

    ;; Return the database with the updated indicator and updated indices
    (assoc db
           :indicators updated-indicators
           :indicator-type->ids updated-types-index
           :indicator-author-name->ids updated-author-name-index)))

(defn- update-indicator
  "Updates an indicator in the db if the new indicator was created after the existing one in the db. Returns the updated db."
  [{:keys [indicators] :as db} indicator]

  ;; author-name and type are expected to not change if an indicator is updated.
  ;; If we find a case where it happened then log it
  ;; and we'd also need to implement updating the indices
  (when-let [existing-indicator (get indicators (:id indicator))]
    (when (not= (:type existing-indicator) (:type indicator))
      ;; Maybe throw an exception here instead?
      ;; And when it is caught, skip updating this indicator
      (println "while indexing type, :type was not an invariant. Found:" existing-indicator "and" indicator))

    ;; TODO it turns out author-name can change
    ;;   if the search endpoint gets implemented should look at handling this
    #_(when (not= (:author-name existing-indicator) (:author-name indicator))
      ;; TODO maybe throw an exception instead?
        (println "while indexing author-name, :author-name was not an invariant. Found:" existing-indicator "and" indicator)))

  ;; Note: the fields we index are expected to not have changed,
  ;; so we don't need to also update the indices in this fn

  (let [id (:id indicator)
        existing-indicator (get indicators id)]
    (if (newer-indicator? indicator existing-indicator)
      ;; Indicator was newer, so update it in the db,
      ;; and return the updated db
      (let [new-indicators (assoc indicators id indicator)]
        (assoc db :indicators new-indicators))

      ;; Indicator was older than existing one in db, so do nothing
      ;; and return the db unchanged
      db)))

(defn- load-indicator
  "Creates or updates an indicator into the database.
   Returns the updated database."
  [{:keys [indicators] :as db} {:keys [id] :as indicator}]
  (if (get indicators id)
    (update-indicator db indicator)
    (create-indicator db indicator)))

(defn- enrich-indicator
  "Add additional data from the pulse to the indicator."
  [indicator pulse]
  (assoc indicator :author-name (:author_name pulse)))

(defn- load-pulse
  "Loads indicators into the database.
   Returns the updated database."
  [acc pulse]
  ;; A pulse has a vector of indicators, we want to enrich the indicators
  ;; with information about the pulse, and then load them into the database
  (let [indicators (:indicators pulse)
        indicators (map #(enrich-indicator % pulse) indicators)]
    (reduce load-indicator acc indicators)))

(defn- build-database [pathname]
  (let [empty-database {:indicators {}
                        :indicator-type->ids {}
                        :indicator-author-name->ids {}}
        file (java.io.File. pathname)
        pulses (json/read-value file json/keyword-keys-object-mapper)]
    (reduce load-pulse empty-database pulses)))


;; In Memory database implementation
(defrecord InMemoryDatabase [filepath data]
  component/Lifecycle
  (start [this]
    (println "Building in memory database from file:" filepath)
    (let [database (build-database filepath)]
      (println "Finished building database with" (count (:indicators database)) "indicators")
      (assoc this :data database)))

  (stop [this]
    (println "Stopping in memory database")
    this))

(defn create
  "Create a component for use in a component system"
  [filepath]
  (component/using (map->InMemoryDatabase {:filepath filepath}) []))

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
