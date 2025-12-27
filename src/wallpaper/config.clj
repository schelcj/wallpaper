(ns wallpaper.config
  "Functions for accessing the configuration settings."
  (:require [wallpaper.constants :as const])
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:require [xdg-rc.core :refer :all])
  (:gen-class))

;; TODO - maybe a way to change the XDG values for testing?
;; (maybe i'm just doing this all wrong?)
;; (defn app-mode
;;   []
;;   (println (System/getProperty "app.mode")))

(def config-file
  "Where the default config file lives on disk."
  (io/file (xdg-config-dir const/APP_NAME) "config.edn"))

(defn default-config-path
  "Path to the default configuration file."
  []
  (str config-file))

(defn construct
  "Creates a map of all the default configuration file locations for caching and such.
  See the default configuration file in `resources/config.edn` for more details
  of each setting and the default value."
  []
  (let [lock (str (io/file (xdg-data-dir const/APP_NAME) "lock"))
        wallpapers-dir (str (io/file (System/getenv "HOME") "Dropbox" "Wallpapers"))
        tiles-dir (str (io/file (System/getenv "HOME") "Dropbox" "Wallpapers" "Tiles"))
        sources (str (io/file (xdg-data-dir const/APP_NAME) "sources.edn"))
        current (str (io/file (xdg-data-dir const/APP_NAME) "current.edn"))
        previous (str (io/file (xdg-data-dir const/APP_NAME) "previous.edn"))
        category (str (io/file (xdg-data-dir const/APP_NAME) "category.edn"))
        history (str (io/file (xdg-cache-dir const/APP_NAME) "history.edn"))
        setter (str (io/file (System/getenv "HOME") "bin" "fbsetbg"))]
    {:lock-file lock
     :wallpapers-dir wallpapers-dir
     :tiles-dir tiles-dir
     :current current
     :previous previous
     :category-file category
     :history history
     :sources sources
     :setter {:path setter :opts {:full "-f" :tiled "-t"}}
     :weights {86400 1000 604800 500 2592000 200}}))

(defn restore!
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
    (.mkdirs (io/file (xdg-data-dir const/APP_NAME)))
    (.mkdirs (io/file (xdg-cache-dir const/APP_NAME)))
    (.mkdirs (io/file (xdg-config-dir const/APP_NAME)))
    (when-not (.exists (io/file (:sources defaults)))
      (spit (io/file (:sources defaults)) ()))
    (when-not (.exists (io/file (:current defaults)))
      (spit (io/file (:current defaults)) ()))
    (when-not (.exists (io/file (:previous defaults)))
      (spit (io/file (:previous defaults)) ()))
    (when-not (.exists (io/file (:history defaults)))
      (spit (io/file (:history defaults)) ()))
    (when-not (.exists config-file)
      (with-open [in (io/input-stream (io/resource "config.edn"))
                  out (io/output-stream config-file)]
        (io/copy in out)))))

(defn init?
  "Has the configuration be initialized?"
  []
  (let [defaults (construct)]
       (and (.exists (io/file (:sources defaults)))
            (.exists (io/file (:current defaults)))
            (.exists (io/file (:previous defaults)))
            (.exists (io/file (:history defaults)))
            (.exists (io/file config-file)))))
