(ns wallpaper.papers
  "Functions to find, filter, weight, and apply the wallpaper."
  (:require [wallpaper.category :as category])
  (:require [wallpaper.history :as history])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :refer [sh]])
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
  "Build seq of wallpapers fitlering out previously displayed wallpapers and directories.

  Arguments:
  - history (File): File for the history cache.
  - wallpapers (seq): All wallpapers that were found for the given categories."
  [history wallpapers]
  (remove (set (history/restore history)) (remove #(.isDirectory (io/file %)) wallpapers)))

(defn weight
  "Applies a weight for the given wallpaper based on the mtime of the file.

  Arguments:
  - wallpaper (String): Full path to an image file to test.
  - weights: (Map): Map of weights to apply where the key is time in seconds and the value is number of times to repeat the image."
  [wallpaper weights]
  (let [now (quot (System/currentTimeMillis) 1000)
        mtime (quot (.lastModified (io/file wallpaper)) 1000)]
    (when-let [weight (some->>
      (keys weights)
      (filter #(< (- now %) mtime))
      seq
      (reduce min))]
    (get weights weight))))

(defn apply-weights
  "Weight the wallpapers based on the mtime of the file so we favor newer images.

  Arguments:
  - wallpapers (seq): All wallpapers that we would like to weight.
  - weights: (Map): Map of weights to apply where the key is time in seconds and the value is number of times to repeat the image."
  [wallpapers weights]
  (mapcat (fn [wallpaper]
    (let [w (or (weight wallpaper weights) 1)]
      (repeat w wallpaper)))
      wallpapers))

(defn random
  "Get a random wallpaper from a list of wallpapers

  Arguments:
  - config (Map): Map of the configuration settings"
  [config]
  (let [sources (category/all (:sources config))
          dirs (dirs (:wallpapers-dir config) sources)
          wallpapers (gather dirs)
          filtered-wallpapers (prune (:history config) wallpapers)
          effective-wallpapers (if (seq filtered-wallpapers)
                                 filtered-wallpapers
                                 (do
                                   (history/clear (:history config))
                                   wallpapers))
          weighted-wallpapers (apply-weights effective-wallpapers (:weights config))]
          (first (shuffle (vec weighted-wallpapers)))))

(defn display
  "Actually set the wallpaper. Records the current wallpaper as the previous, sets the new current
  to the passed image, records the passed image in the history, and sets the actual display background.

  Arguments:
  - config (Map): Map of the configuration settings
  - image (String): Path to a image file to set as the wallpaper"
  [config wallpaper]
  (sh "fbsetbg" "-f" wallpaper)
  (history/set-previous (:current config) (:previous config))
  (history/set-current (:current config) wallpaper)
  (history/record (:history config) wallpaper))
