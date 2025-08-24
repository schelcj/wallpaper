(ns wallpaper.category-test
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [wallpaper.category :as cat]
            [clojure.test :refer :all]))

(import '[java.io File])

(def category-file (File/createTempFile "test-category-" ".edn"))
(def sources-file (-> "resources/fixtures/sources" io/resource))
(def categories (string/split-lines (slurp sources-file)))

(deftest test-all
  (testing "Loading all categories")
  (is (= (count categories) (count (cat/all sources-file)))))

(deftest test-record
  (testing "Recording a catgory")
  (cat/record category-file "foo")
  (is (.exists category-file))
  (pos? (.length category-file)))

(deftest test-clear
  (testing "Clearing a set category")
  (cat/clear category-file)
  (is (not (.exists category-file))))

(.deleteOnExit category-file)
