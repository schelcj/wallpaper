(ns wallpaper.papers
  "Functions to find, filter, weight, and apply the wallpaper."
  (:require [wallpaper.history :as history])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn dirs
  "Build a seq of all the directories to search for wallpapers in.

  Arguments:
  - base (File): File object for the base directory where all wallpapers are stored.
  - sources (vector): Potential directory supplied on command line via the --category flag."
  [base sources]
  (vec (map #(io/file base %) sources)))

(defn gather
  "Build a seq of all the available wallpapers on disk.

  Arguments:
  - dirs (vector): All directories to search for wallpapers."
  [dirs]
  (loop [dirs dirs result []]
    (if (empty? dirs)
      (apply concat result)
      (recur (rest dirs) (conj result (map #(.getPath %) (file-seq (first dirs))))))))

(defn prune
  "Build seq of wallpapers fitlering out previously displayed wallpapers

  Arguments:
  - history (File): File for the history cache.
  - wallpapers (seq): All wallpapers that were found for the given categories."
  [history wallpapers]
  (remove (set (history/restore history)) wallpapers))

;; TODO - set weights on wallpapers based on ctime
;; previous weights:
;;    age_in_secs => weight
;;    86400   => 1000,
;;    604800  => 500,
;;    2592000 => 200,
;;
;; if ((epoch - age_in_secs) < mtime) then push weight number of copies onto papers of the given paper
;;
;; use the mtime of the image to compare to the current time minus the weighted time and if weighted time is less than
;; the mtime repeat that image path weight times and push onto the papers seq. do this for all images in the seq.
;;
;; used ctime in the perl version, which is the same in the case of these images since i never touch them once downloaded.
;;
;; (< (- (quot (System/currentTimeMillis) 1000) 86400) (.lastModified (io/file new-wallpaper))
;; (take 5 (repeat (last wallpapers))
;; (conj (take 10 (repeat (first wallpapers) wallpapers)))
(defn weight
  "Weight the wallpapers based on the mtime of the file so we favor newer images.

  Arguments:
  - wallpapers (seq): All wallpapers that we would like to weight."
  [wallpapers]
  wallpapers)

(defn random
  "Get a random wallpaper from a list of wallpapers"
  [wallpapers]
  (first (shuffle (vec wallpapers))))
