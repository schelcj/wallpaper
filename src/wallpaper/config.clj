(ns wallpaper.config
  "Functions for accessing the configuration settings."
  (:require [wallpaper.constants :as const])
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as s])
  (:require [clojure.walk :as walk])
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

(defn placeholder-paths
  "Creates a map of the default paths for XDG values and HOME.
  The map consists of essentially template variable placeholders
  for the values that we need to replace with xdg values and env
  vars such as `$HOME`. The format is `{{var_name}} path`. For
  example:

  {{home}}          (System/getenv HOME)
  {{xdg-data-dir}}  (xdg-data-dir const/APP_NAME)
  {{xdg-cache-dir}} (xdg-cache-dir const/APP_NAME)"
  []
  (let [home (str (io/file (System/getenv "HOME")))
        data-dir (str (io/file (xdg-data-dir const/APP_NAME)))
        cache-dir (str (io/file (xdg-cache-dir const/APP_NAME)))]
    {"{{home}}" home
     "{{xdg-data-dir}}" data-dir
     "{{xdg-cache-dir}}" cache-dir}))

(defn apply-placeholders
  "Takes the default configuration, walks through all the values and replaces the values
  from `placeholder-paths`.

  Arguments:
  - config (map): default configuration to apply any placeholder replacements"
  [config]
  (let [replacements (placeholder-paths)]
    (walk/postwalk
     (fn [x]
       (if (string? x)
         (reduce (fn [s [from to]]
                   (s/replace s from to))
                 x
                 replacements)
         x))
     config)))

(defn restore!
  "Load the configuration file from disk and merge with the default configuration settings."
  []
  (let [userconfig (if (.exists config-file)
                     (edn/read-string (slurp config-file))
                     ())
        defaults (edn/read-string (slurp (io/resource "config.edn")))]
    (merge (apply-placeholders defaults) userconfig)))

(defn init!
  "Create all the initial configuration, cache, and state files and directories"
  []
  (let [defaults (restore!)]
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
  (let [defaults (restore!)]
    (and (.exists (io/file (:sources defaults)))
         (.exists (io/file (:current defaults)))
         (.exists (io/file (:previous defaults)))
         (.exists (io/file (:history defaults)))
         (.exists (io/file config-file)))))
