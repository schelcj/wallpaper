(ns wallpaper.stats
  "Functions for stats about the library."
  (:require [wallpaper.category :as category])
  (:require [wallpaper.history :as history])
  (:require [wallpaper.papers :as papers])
  (:require [clojure.java.io :as io])
  (:require [clojure.pprint :refer [print-table]])
  (:gen-class))

(defn total-wallpapers-available
  "The count of all wallpapers available in the currently configured catgories"
  []
  (let [categories (category/all!)
        dirs (papers/dirs categories)]
    (count (papers/gather dirs))))

(defn total-wallpapers-available-by-category
  "The count of wallpapers available in the current configured categories broken down by category."
  []
  (let [categories (category/all!)
        dirs (papers/dirs categories)]
    (zipmap
     (map #(history/get-relative-path (.getPath %)) dirs)
     (map #(count (papers/gather [%])) dirs))))

(defn total-wallpapers-displayed
  "The count of wallpapers that have been displayed already."
  []
  (count (history/restore!)))

(defn total-wallpapers-displayed-by-category
  "The count of wallpapers displayed by category."
  []
  (let [displayed (history/restore!)]
    (->> displayed
         (map #(-> % io/file .getParentFile .getName))
         frequencies)))

(defn overall
  "Return map of various counts, totals, and such..."
  []
  {:total-wallpapers-available (total-wallpapers-available)
   :total-wallpapers-displayed (total-wallpapers-displayed)
   :total-wallpapers-available-by-category (total-wallpapers-available-by-category)
   :total-wallpapers-displayed-by-category (total-wallpapers-displayed-by-category)})

(defn print-stats!
  ""
  []
  (let [totals (overall)
        available (:total-wallpapers-available-by-category totals)
        displayed (:total-wallpapers-displayed-by-category totals)
        summary   (for [[cat total] available
                        :let [shown (get displayed cat 0)
                              remain (- total shown)
                              pct (double (* 100 (/ shown total)))]]
                    {:category cat
                     :available total
                     :displayed shown
                     :remaining remain
                     :percent (format "%.1f%%" pct)})
        total-available (:total-wallpapers-available totals)
        total-displayed (:total-wallpapers-displayed totals)
        totals {:category "TOTAL"
                :available total-available
                :displayed total-displayed
                :remaining (- total-available total-displayed)
                :percent (format "%.1f%%" (double (* 100 (/ total-displayed total-available))))}
        separator {:category "----" :available "" :displayed "" :remaining "" :percent ""}
        results (conj (vec (reverse (sort-by :percent summary))) separator totals)]
    (print-table [:category :available :displayed :remaining :percent] results)))
