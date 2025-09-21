(ns wallpaper.config
  "Functions for accessing the configuration settings."
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:require [xdg-rc.core :refer :all])
  (:gen-class))

;; TODO - naming things is hard...
(def app-name "wallpaper")

(def config-file
  "Where the default config file lives on disk."
  (io/file (xdg-config-dir app-name) "config.edn"))

(defn default-config-path
  "Path to the default configuration file."
  []
  (str config-file))

(defn construct
  "Creates a map of all the default configuration file locations for caching and such.

  Default configuration values and their locations:

  Where to store the category to limit selection.
  :category-file '$XDG_DATA_DIR/wallpaper/category.edn',

  A set of categories (i.e. directories) under the wallpaper
  root directory to limit the selection to if no category
  was specifically choosen with `--category`. This prevents
  displaying xmas images out of season or showing the propaganda
  tiles in a full screen mode without tiling.
  :sources '$XDG_DATA_DIR/wallpaper/sources.edn'

  Records all wallpapers that have been displayed previously
  since the last cache flush.
  :history '$XDG_CACHE_DIR/wallpaper/history.edn'

  Records the currently displayed wallpaper
  :current '$XDG_DATA_DIR/wallpaper/current.edn'

  Records the most recently displayed wallpaper
  :previous '$XDG_DATA_DIR/wallpaper/previous.edn'

  Lock file to prevent switching the wallpaper.
  :lock-file '$XDG_DATA_DIR/wallpaper/lock'

  Weights to apply to favor newer images. The format is
  age => weight where age is seconds based on the file mtime
  and the weight is the number of times to repeat the image
  in the list of possible wallpapers.
  :weights {86400 1000, 604800 500, 2592000 200}
  "
  []
  (let [lock (str (io/file (xdg-data-dir app-name) "lock"))
        sources (str (io/file (xdg-data-dir app-name) "sources.edn"))
        current (str (io/file (xdg-data-dir app-name) "current.edn"))
        previous (str (io/file (xdg-data-dir app-name) "previous.edn"))
        category (str (io/file (xdg-data-dir app-name) "category.edn"))
        history (str (io/file (xdg-cache-dir app-name) "history.edn"))]
    {:lock-file lock
     :current current
     :previous previous
     :category-file category
     :history history
     :sources sources
     :weights {86400 1000, 604800 500, 2592000 200}}))

(defn restore
  "Load the configuration file from disk and merge with the default configuration settings."
  []
  (conj (construct) (edn/read-string (slurp config-file))))

(defn init!
  "Create all the initial configuration, cache, and state files and directories"
  []
  (let [defaults (construct)]
    (.mkdirs (io/file (xdg-data-dir app-name)))
    (.mkdirs (io/file (xdg-cache-dir app-name)))
    (.mkdirs (io/file (xdg-config-dir app-name)))
    (spit (io/file (:sources defaults)) ())
    (spit (io/file (:current defaults)) ())
    (spit (io/file (:previous defaults)) ())
    (spit (io/file (:history defaults)) ())))
