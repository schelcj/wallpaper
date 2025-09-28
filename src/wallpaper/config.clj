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
  See the default configuration file in `resources/config.edn` for more details
  of each setting and the default value."
  []
  (let [lock (str (io/file (xdg-data-dir app-name) "lock"))
        wallpapers-dir (str (io/file (System/getenv "HOME") "Dropbox" "Wallpapers"))
        sources (str (io/file (xdg-data-dir app-name) "sources.edn"))
        current (str (io/file (xdg-data-dir app-name) "current.edn"))
        previous (str (io/file (xdg-data-dir app-name) "previous.edn"))
        category (str (io/file (xdg-data-dir app-name) "category.edn"))
        history (str (io/file (xdg-cache-dir app-name) "history.edn"))
        setter (str (io/file (System/getenv "HOME") "bin" "fbsetbg"))]
    {:lock-file lock
     :wallpapers-dir wallpapers-dir
     :current current
     :previous previous
     :category-file category
     :history history
     :sources sources
     :setter {:path setter :opts {:full "-f" :tiled "-t"}}
     :weights {86400 1000 604800 500 2592000 200}}))

(defn restore
  "Load the configuration file from disk and merge with the default configuration settings."
  []
  (let [config (if (.exists config-file)
                 (edn/read-string (slurp config-file))
                 ())]
    (conj (construct) config)))

(defn init!
  "Create all the initial configuration, cache, and state files and directories"
  []
  (let [defaults (construct)]
    (.mkdirs (io/file (xdg-data-dir app-name)))
    (.mkdirs (io/file (xdg-cache-dir app-name)))
    (.mkdirs (io/file (xdg-config-dir app-name)))
    (when-not (.exists (io/file (:sources defaults)))
      (spit (io/file (:sources defaults)) ()))
    (when-not (.exists (io/file (:current defaults)))
      (spit (io/file (:current defaults)) ()))
    (when-not (.exists (io/file (:previous defaults)))
      (spit (io/file (:previous defaults)) ()))
    (when-not (.exists (io/file (:history defaults)))
      (spit (io/file (:history defaults)) ()))
    (when-not (.exists config-file)
      (io/copy (io/file (io/resource "config.edn")) config-file))))
