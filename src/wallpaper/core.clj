(ns wallpaper.core
  (:require [wallpaper.history :as history])
  (:require [wallpaper.category :as category])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :refer [sh]])
  (:gen-class))

(def prefix (io/file (System/getenv "HOME") ".wallpapers"))

(def config {:lock-file (io/file prefix "lock")
             :category-file (io/file prefix "category.edn")
             :wallpapers-dir (io/file prefix "Wallpapers")
             :current (io/file prefix "current.edn")
             :previous (io/file prefix "previous.edn")
             :history (io/file prefix "history.edn")
             :sources (io/file prefix "sources")
             :default-category "all"})

(def cli-options
  [["-c" "--category CATEGORY" "Wallpaper category"]
   ["-f" "--flush-cache" "Flush the wallpaper history cache"]
   ["-d" "--dump-cache" "Print current wallpaper history cache to STDOUT"]
   ["-l" "--lock" "Lock the current wallpaper"]
   ["-u" "--unlock" "Unlock the current wallpaper"]
   ["-p" "--previous" "Set the wallpaper to the previous image"]
   ["-i" "--image IMAGE" "Set the provided image as the current wallpaper"]
   ["-t" "--tile TILE" "Tiled the provided image as the wallpaper"]
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

(defn set-lockfile
  "Set the lockfile to keep the current wallpaper from being changed."
  []
  (spit (:lock-file config) ""))

(defn clear-lockfile
  "Clear the lockfile to all the current wallpaper to be changed."
  []
  (.delete (io/file (:lock-file config))))

(defn wallpaper-dirs
  "Build a seq of all the directories to search for wallpapers in.

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
  (remove (set (history/load (:history config))) wallpapers))

;; TODO - set weights on wallpapers based on ctime
;; previous weights:
;;    age_in_secs => weight
;;    86400   => 1000,
;;    604800  => 500,
;;    2592000 => 200,
;;
;; if ((epoch - age_in_secs) < mtime) then push weight number of copies onto papers of the given paper
;;
;; (< (- (quot (System/currentTimeMillis) 1000) 86400) (.lastModified (io/file new-wallpaper))
(defn weight-wallpapers
  "Weight the wallpapers based on the mtime of the file so we favor newer images.

  Arguments:
  - wallpapers (seq): All wallpapers that we would like to weight."
  [wallpapers]
  wallpapers)

(defn random-wallpaper
  "Get a random wallpaper from a list of wallpapers"
  [wallpapers]
  (first (shuffle (vec wallpapers))))

(defn set-wallpaper
  "Actually set the wallpaper."
  []
  (let [
    sources (category/all (:sources config))
    dirs (wallpaper-dirs sources)
    wallpapers (load-wallpapers dirs)
    filtered-wallpapers (filter-wallpapers wallpapers)
    weighted-wallpapers (weight-wallpapers filtered-wallpapers)
    new-wallpaper (random-wallpaper weighted-wallpapers)
    ]
    (println (str "fbsetbg -f " new-wallpaper))
    (sh "fbsetbg" "-f" new-wallpaper)
    (history/set-previous (:current config) (:previous config))
    (history/set-current (:current config) new-wallpaper)
    (history/record (:history config) new-wallpaper)))
    ;; (System/exit 0)

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
        (do
          (println (usage summary))
          (System/exit 0))
      (:lock options)
        (do (set-lockfile)
            (System/exit 0))
      (:unlock options)
        (do (clear-lockfile))
      (:category options)
        (do
          (category/record (:category-file config) (:category options)))
      (:dump-cache options)
        (do
          (history/dump (:history config)))
        ;; (System/exit 0)
      (:flush-cache options)
        (do
          (history/clear (:history config)))
          ;; (System/exit 0)
      (:previous options)
        (do
          ;; TODO
          (println "set the wallpaper to the contents of the previous file"))
      (:image options)
        (do
          ;; TODO
          (println "set the wallpaper to the given image file"))
      (:tile options)
        (do
          (println "set the wallpaper to the given image tiled"))
      (:clear options)
        (do
          (category/clear (:category-file config))))
    (set-wallpaper)))
