(ns wallpaper.config
  "Functions for accessing the configuration settings."
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:gen-class))

(def prefix
  "Base directory where configurations will live. Use $XDG_CONFIG_HOME by default
  and if that is not set in the environment we will use $HOME/.config/"
  (let [base (or (System/getenv "XDG_CONFIG_HOME") (str (System/getenv "HOME") "/.config"))]
    (io/file base "wallpaper")))

(def config-file
  "Where the default config file lives on disk."
  (io/file prefix "config.edn"))

(defn restore
  "Load the configuration file from disk"
  []
  (edn/read-string (slurp config-file)))
