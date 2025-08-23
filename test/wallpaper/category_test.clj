(ns wallpaper.category-test
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [wallpaper.category :as cat]
            [clojure.test :refer :all]))

(import '[java.io File])

(def category-file (File/createTempFile "test-category-" ".edn"))
(def sources-file (-> "resources/fixtures/sources" io/resource))
(def categories (string/split-lines (slurp sources-file)))

;; pass file and optional category
(deftest test-all
  (testing "Loading all categories")
  (is (= (count categories) (count (cat/all sources-file)))))

;; pass file and category
;; can recored category
;; file exists and is non-zero length
(deftest test-set
  (testing "Setting a catgory"))

;; pass file
;; can clear category
;; file does not exist
(deftest test-clear
  (testing "Clearing a set category"))

(.deleteOnExit category-file)
