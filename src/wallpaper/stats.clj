(ns wallpaper.stats
  "Functions for stats about the library."
  (:require [wallpaper.category :as category])
  (:require [wallpaper.config :as config])
  (:require [wallpaper.history :as history])
  (:require [wallpaper.papers :as papers])
  (:gen-class))

(defn total-wallpapers-available
  "The count of all wallpapers available in the currently configured catgories"
  []
  (let [config (config/restore!)
        sources (category/all!)
        dirs (papers/dirs sources)
        wallpapers (papers/gather dirs)]
    (count wallpapers)))

(defn total-wallpapers-available-by-category
  "The count of wallpapers available in the current configured categories broken down by category."
  []
  (let [config (config/restore!)
        sources (category/all!)
        dirs (papers/dirs sources)
        wallpapers (papers/gather dirs)]
    (zipmap
     (map #(history/get-relative-path (.getPath %)) dirs)
     (map #(count (papers/gather [%])) dirs))))

(defn total-wallpapers-displayed
  "The count of wallpapers that have been displayed already."
  []
  (let [config (config/restore!)
        history (history/restore!)]
    (count history)))

(defn total-wallapers-displayed-by-category
  "The count of wallpapers displayed by category."
  []
  (println "total-wallpapers-displayed-by-category"))

(defn overall
  "Return map of various counts, totals, and such..."
  []
  {:total-wallpapers-available (total-wallpapers-available)
   :total-wallpapers-displayed (total-wallpapers-displayed)
   :total-wallpapers-available-by-category (total-wallpapers-available-by-category)})
