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

(def config-dir
  "Path to the XDG_CONFIG_DIR for this application (default: /home/$USER/.config/<app_name>)"
  (xdg-config-dir const/APP_NAME))

(def cache-dir
  "Path to the XDG_CACHE_DIR for this application (default: /home/$USER/.cache/<app_name>)"
  (xdg-cache-dir const/APP_NAME))

(def data-dir
  "Path to the XDG_DATA_DIR for this application (default: /home/$USER/.local/share/<app_name>)"
  (xdg-data-dir const/APP_NAME))

(def config-file
  "Where the users config file lives on disk (default: /home/$USER/.config/<app_name>/config.edn)"
  (io/file (s/join "/" [config-dir "config.edn"])))

(defn placeholder-paths
  "Creates a map of the default paths for XDG values and HOME.
  The map consists of essentially template variable placeholders
  for the values that we need to replace with xdg values and env
  vars such as `$HOME`. The format is `{{var_name}} path`."
  []
  (let [home (str (io/file (System/getenv "HOME")))]
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
    (merge (apply-placeholders defaults) (apply-placeholders userconfig))))

(defn create-default-config!
  "Creates a local user copy of the default configuration from the resources in the $XDG_CONFIG_DIR."
  []
  (when-not (.exists config-file)
    (with-open [in (io/input-stream (io/resource "config.edn"))
                out (io/output-stream config-file)]
      (io/copy in out))))

(defn default-files
  "Generates a vector of the default files from the config that we need to create if they do not exist."
  []
  (let [defaults (restore!)
        {:keys [current previous history]} defaults
        paths [current previous history]]
    paths))

(defn create-file
  "Creates a given file if it does not already exist.

  Arguments:
  - file (string): path to file that should be created"
  [path]
  (let [file (io/file path)]
    (when-not (.exists file)
      (spit file ()))))

(defn preflight-check-files!
  "check if all the default files exists and if not create."
  []
  (let [defaults (restore!)
        files (default-files)]
    (map create-file files)))

(defn preflight-check-dirs!
  "check if all the default directories exists and if not create."
  []
  (let [defaults (restore!)
        dirs [data-dir cache-dir config-dir]]
    (map #(.mkdir (io/file %)) dirs)))

(defn preflight-check!
  "check that all files/dirs exists before starting"
  []
  (preflight-check-dirs!)
  (preflight-check-files!))
