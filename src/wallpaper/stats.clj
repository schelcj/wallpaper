(ns wallpaper.stats
  "Functions for stats about the library."
  (:require [wallpaper.category :as category])
  (:require [wallpaper.history :as history])
  (:require [wallpaper.papers :as papers])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn total-wallpapers-available
  "The count of all wallpapers available in the currently configured catgories"
  []
  (let [sources (category/all!)
        dirs (papers/dirs sources)]
    (count (papers/gather dirs))))

(defn total-wallpapers-available-by-category
  "The count of wallpapers available in the current configured categories broken down by category."
  []
  (let [sources (category/all!)
        dirs (papers/dirs sources)]
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


