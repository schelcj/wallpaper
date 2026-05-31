(ns wallpaper.category-test
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [wallpaper.category :as cat]
            [clojure.test :refer :all]))

(import '[java.io File])

;; TODO
;; so many side-effects, how do i even test this?
;; # how to test with (config/restore)?
;; will have the defaults to override somehow
;;
;; # functions to test
;; - all!
;; - record!
;; - clear!
;; - add-category!
;; - del-category!
;; - show-categories!

;; (def category-file (File/createTempFile "test-category-" ".edn"))
;; (def categories ())

;; (deftest test-record!
;;   (testing "Recording a catgory")
;;   (cat/record! category-file "foo")
;;   (is (.exists category-file))
;;   (pos? (.length category-file)))

;; (deftest test-clear!
;;   (testing "Clearing a set category")
;;   (cat/clear! category-file)
;;   (is (not (.exists category-file))))

;; (
;;  .deleteOnExit category-file)
