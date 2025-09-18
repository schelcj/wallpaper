(ns wallpaper.papers
  "Functions to find, filter, weight, and apply the wallpaper."
  (:require [wallpaper.config :as config])
  (:require [wallpaper.category :as category])
  (:require [wallpaper.history :as history])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :refer [sh]])
  (:gen-class))

(defn dirs
  "Build a seq of all the directories to search for wallpapers in.

  Arguments:
  - sources (vector): Potential directory supplied on command line via the --category flag."
  [sources]
  (let [config (config/restore)]
    (vec (map #(io/file (:wallpapers-dir config) %) sources))))

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
  - wallpapers (seq): All wallpapers that were found for the given categories."
  [wallpapers]
  (let [config (config/restore)]
    (remove (set (history/restore)) (remove #(.isDirectory (io/file %)) wallpapers))))

(defn weight
  "Applies a weight for the given wallpaper based on the mtime of the file.

  Arguments:
  - wallpaper (String): Full path to an image file to test."
  [wallpaper]
  (let [now (quot (System/currentTimeMillis) 1000)
        mtime (quot (.lastModified (io/file wallpaper)) 1000)
        config (config/restore)]
    (when-let [weight (some->>
                       (keys (:weights config))
                       (filter #(< (- now %) mtime))
                       seq
                       (reduce min))]
      (get (:weights config) weight))))

(defn apply-weights
  "Weight the wallpapers based on the mtime of the file so we favor newer images.

  Arguments:
  - wallpapers (seq): All wallpapers that we would like to weight."
  [wallpapers]
  (mapcat (fn [wallpaper]
            (let [config (config/restore)
                  w (or (weight wallpaper) 1)]
              (repeat w wallpaper)))
          wallpapers))

(defn random
  "Get a random wallpaper from a list of wallpapers filtering out previously displayed papers and applying
  weighting to favor new images."
  []
  (let [config (config/restore)
        sources (category/all)
        dirs (dirs sources)
        wallpapers (gather dirs)
        filtered-wallpapers (prune wallpapers)
        effective-wallpapers (if (seq filtered-wallpapers)
                               filtered-wallpapers
                               (do
                                 (history/clear)
                                 wallpapers))
        weighted-wallpapers (apply-weights effective-wallpapers)]
    (first (shuffle (vec weighted-wallpapers)))))

(defn display
  "Actually set the wallpaper. Records the current wallpaper as the previous, sets the new current
  to the passed image, records the passed image in the history, and sets the actual display background.

  Arguments:
  - image (String): Path to a image file to set as the wallpaper"
  [wallpaper tiled]
  (let [config (config/restore)]
    (sh "fbsetbg" "-f" wallpaper)
    (history/set-previous)
    (history/set-current wallpaper)
    (history/record wallpaper)))

(defn fullscreen
  [wallpaper]
  (sh "fbsetbg" "-f" wallpaper))

(defn tiled
  [wallpaper]
  (sh "fbsetbg" "-t" wallpaper))
