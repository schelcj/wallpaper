(ns wallpaper.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:gen-class))

(def prefix (io/file (System/getenv "HOME") ".wallpapers"))

(def config {:lock-file (io/file prefix "lock")
             :category-file (io/file prefix "category")
             :wallpapers-dir (io/file prefix "Wallpapers")
             :current (io/file prefix "current")
             :previous (io/file prefix "previous")
             :history (io/file prefix "history.edn")
             :sources (io/file prefix "sources")
             :default-category "all"})

;; Optional arguments
;; - category: passed update category file on disk then let normal flow happen
;; - clear: delete the category file on disk then let normal flow happen
;; - flush-cache: delete history file then let normal flow happen
;; - lock: write lock file and exit
;; - unlock: delete lock file and let normal flow happen
;; - previous: read contents of previous file, set the wallpaper to this wallpaper, exit
;; - image: set the wallpaper to the given file path and exit
;; - dump-cache: print contents of the history to STDOUT and exit
(def cli-options
  [["-c" "--category" "Wallpaper category"
    :default (:default-category config)
    :parse-fn str]
   ["-f" "--flush-cache" "Flush the wallpaper history cache"]
   ["-d" "--dump-cache" "Print current wallpaper history cache to STDOUT"]
   ["-l" "--lock" "Lock the current wallpaper"]
   ["-u" "--unlock" "Unlock the current wallpaper"]
   ["-p" "--previous" "Set the wallpaper to the previous image"]
   ["-i" "--image" "Set the provided image as the current wallpaper"]
   ["-t" "--tile" "Tiled the provided image as the wallpaper"]
   ["-r" "--clear" "Clear the previous wallpaper category"]
   ["-h" "--help" "Show help"]])

(defn usage [options-summary]
  (->> ["wallpaper"
        ""
        "Usage: wallpaper [options]"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn categories
  [& category]
  (if category
    (vec category)
    (with-open [r (io/reader (:sources config))]
      (vec (line-seq r)))))

(defn load-history
  "Read the history of previously displayed wallpapers."
  []
  (edn/read-string (slurp (:history config))))

(defn record-history
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - wallpapers (vector): Wallpapers that have been displayed to be recorded for subsequent runs."
  [wallpapers]
  (spit (:history config) (pr-str wallpapers)))

(defn wallpaper-dirs
  "Build a deq of all the directories to search for wallpapers in.

  Arguments:
  - sources (vector): Potential directory supplied on command line via the --category flag."
  [sources]
  (vec (map #(io/file (:wallpapers-dir config) %) sources)))

(defn load-wallpapers
  "Build a seq of all the available wallpapers on disk.

  Arguments:
  - dirs (vector): All directories to search for wallpapers."
  [dirs]
  (loop [dirs dirs result []]
    (if (empty? dirs)
      (apply concat result)
      (recur (rest dirs) (conj result (map #(.getPath %) (file-seq (first dirs))))))))

(defn filter-wallpapers
  "Build seq of wallpapers fitlering out previously displayed wallpapers

  Arguments:
  - wallpapers (seq): All wallpapers that were found for the given categories."
  [wallpapers]
  (remove (set (load-history)) wallpapers))

(defn random-wallpaper
  "Get a random wallpaper from a list of wallpapers"
  [wallpapers]
  (first (shuffle (vec wallpapers))))

;; TODO
;; - if the lock file exists exit making no changes
;; - build list of directories to search for wallpapers
;;   - could be a single directory if passed a category via getopt
;;   - could be a single directory if category file exists and directory exists
;;   - if no arg and no file use contents of the default :sources file
;; - build list of wallpapers in the directories built previously
;; - load history of previously displayed wallpapers
;; - filter out previously displayed from the list of wallpapers to create list of candidates
;; - apply weights to the list of wallpapers according to ctime of the wallpaper to build new list
;; - select random wallpaper from the filtered and weighted list
;; - use setter to display wallpaper
;; - rename the current file to previous file
;; - add current wallpaper to current file
;; - add wallpaper to the history
;; - record new history to disk
;; - exit
;;
(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (do (println (usage summary))
          (System/exit 0))
      (:lock options)
      (do (println "set lockfile")
          (System/exit 0)))))
