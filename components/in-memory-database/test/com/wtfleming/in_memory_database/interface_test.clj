(ns com.wtfleming.in-memory-database.interface-test
  (:require [clojure.test :as test :refer :all]
            [com.wtfleming.in-memory-database.interface :as db]))

(def indicator-fixture-1
  {:indicator "85.93.20.243"
   :description ""
   :created "2018-07-09T18:02:40"
   :title ""
   :content ""
   :type "IPv4"
   :id 1})
(def indicator-fixture-2
  {:indicator "71.24.15.164"
   :description ""
   :created "2019-07-10T18:02:40"
   :title ""
   :content ""
   :type "YARA"
   :id 2})

(def indicator-fixture-3
  {:indicator "91.92.33.129"
   :description ""
   :created "2020-07-11T18:02:40"
   :title ""
   :content ""
   :type "IPv4"
   :id 3})

(def db-fixture
  {:data {;; Vector of all indicators
          :indicators [indicator-fixture-1 indicator-fixture-2 indicator-fixture-3]

          ;; Map for doing lookups by id
          :indicator-idx-by-id {1 0
                                2 1
                                3 2}

          ;; Map representing an inverted index of indicators by type
          :indicator-idxs-by-type {"YARA" [1]
                                   "IPv4" [0 2]}}})

(def empty-db-fixture
  {:data {:indicators []
          :indicator-idx-by-id {}
          :indicator-idxs-by-type {}}})

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

