(ns com.wtfleming.in-memory-database.interface-test
  (:require [clojure.test :as test :refer :all]
            [com.stuartsierra.component :as component]
            [com.wtfleming.in-memory-database.interface :as db]))

;; These first tests build a database in a fixture that
;; more serves as documentation of what the db internals
;; should look like.
;; Later tests in this file instantiate a component and
;; load a database from a json file and are more accurate to what
;; a running system looks like

(def indicator-fixture-1
  {:indicator "85.93.20.243"
   :description ""
   :created "2018-07-09T18:02:40"
   :title ""
   :content ""
   :type "IPv4"
   :author-name "scottlsattler"
   :id 1})

(def indicator-fixture-2
  {:indicator "71.24.15.164"
   :description ""
   :created "2019-07-10T18:02:40"
   :title ""
   :content ""
   :type "YARA"
   :author-name "scottlsattler"
   :id 2})

(def indicator-fixture-3
  {:indicator "91.92.33.129"
   :description ""
   :created "2020-07-11T18:02:40"
   :title ""
   :content ""
   :type "IPv4"
   :author-name "AlienVault"
   :id 3})

(def db-fixture
  {:data {;; Map of all indicators, with indicator id as key
          :indicators {1 indicator-fixture-1
                       2 indicator-fixture-2
                       3 indicator-fixture-3}

          ;; Map representing an inverted index of indicators by type
          :indicator-type->ids {"YARA" #{2}
                                "IPv4" #{1 3}}

          ;; Map representing an inverted index of indicators by author-name
          :indicator-author-name->ids {"scottlsattler" #{1 2}
                                       "AlienVault" #{3}}}})

(def empty-db-fixture
  {:data {:indicators []
          :indicator-type->ids {}}})

(deftest get-indicator-by-id-test
  (testing "a valid id returns the indicator"
    (is (= (db/get-indicator-by-id db-fixture 1) indicator-fixture-1))
    (is (= (db/get-indicator-by-id db-fixture 2) indicator-fixture-2))
    (is (= (db/get-indicator-by-id db-fixture 3) indicator-fixture-3)))

  (testing "an id that does not exist returns nil"
    (is (= (db/get-indicator-by-id db-fixture 111) nil))))

(deftest get-all-indicators-test
  (testing "returns all indicator"
    (is (= (db/get-all-indicators db-fixture) [indicator-fixture-1 indicator-fixture-2 indicator-fixture-3])))

  (testing "an empty database returns an empty vector"
    (is (= (db/get-all-indicators empty-db-fixture) []))))

(deftest get-all-indicators-by-type-test
  (testing "returns a vector of indicators with a type"
    (is (= (db/get-all-indicators-by-type db-fixture "YARA") [indicator-fixture-2]))
    (is (= (db/get-all-indicators-by-type db-fixture "IPv4") [indicator-fixture-1 indicator-fixture-3])))

  (testing "a type that is not present returns an empty vector"
    (is (= (db/get-all-indicators-by-type db-fixture "does-not-exist") []))))

;; -------------------------
;; Tests below this load the database from a json file

;; Note: Probably want to instead use-fixtures :once
;; so we don't build this on each test
;; https://clojuredocs.org/clojure.test/use-fixtures
(defn db-fixture-from-file []
  (-> (component/system-map :db (db/create "indicators.json"))
      component/start
      :db))

(deftest component-get-indicator-by-id-test
  (let [db-component (db-fixture-from-file)]
    (is (= (db/get-indicator-by-id db-component 280142346)
           {:description "", :author-name "AlienVault", :content "", :type "CVE", :created "2018-07-05T18:45:56", :title "", :id 280142346, :indicator "CVE-2017-8291"}))))

(deftest component-get-all-indicators-test
  (let [db-component (db-fixture-from-file)
        all-indicators (db/get-all-indicators db-component)]
    (is (= (count all-indicators) 11498))))

(deftest component-get-all-indicators-by-type-test
  (let [db-component (db-fixture-from-file)
        all-indicators-by-type (db/get-all-indicators-by-type db-component "YARA")]
    (is (= (count all-indicators-by-type) 2))

    (is (every? #(= "YARA" (:type %)) all-indicators-by-type))))
