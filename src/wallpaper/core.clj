(ns wallpaper.core
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io]))

;; TODO - getopts arguments to implement
;; (don't know how getopts works yet in clojure)
;;
;;    -c, --category    Wallpaper category
;;    -f, --flush-cache Flush the wallpaper cache
;;    -d, --dump-cache  Dump the current wallpaper cache to STDOUT
;;    -l, --lock        Lock the current wallpaper
;;    -u, --unlock      Unlock the current wallpaper
;;        --clear       Clear the previous wallpaper category
;;    -p, --previous    Set the wallpaper to the previous paper
;;    -i, --image       Set image as the current wallpaper

(def prefix (io/file (System/getenv "HOME") ".wallpapers"))

(def config {
  :lock-file (io/file prefix "lock")
  :category-file (io/file prefix "category")
  :wallpapers-dir (io/file prefix "Wallpapers")
  :current (io/file prefix "current")
  :previous (io/file prefix "previous")
  :history (io/file prefix "history.edn")
})

(defn record-history
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - wallpapers (vector): Wallpapers that have been displayed to be recorded for subsequent runs."
  [wallpapers]
  (spit (:history config) (pr-str wallpapers)))

(defn load-history
  "Read the history of previously displayed wallpapers."
  []
  (edn/read-string (slurp (:history config))))

;; TODO - load only wallpapers form optional category (i.e. directory)
(defn load-wallpapers
  "Build a seq of all the available wallpapers on disk."
  []
  (map #(.getPath %) (file-seq (:wallpapers-dir config))))

(defn filter-wallpapers
  "Build seq of wallpapers fitlering out previously displayed wallpapers"
  []
  (vec (remove (set (load-history)) (load-wallpapers))))

(defn random-wallpaper
  "Get a random wallpaper from a list of wallpapers"
  []
  (first (shuffle (vec (filter-wallpapers)))))
