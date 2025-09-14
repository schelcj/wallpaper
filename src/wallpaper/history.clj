(ns wallpaper.history
  "Functions for history cache handling."
  (:require [wallpaper.config :as config])
  (:require [clojure.edn :as edn])
  (:require  [clojure.java.io :as io])
  (:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn restore
  "Read the history of previously displayed wallpapers."
  []
  (let [config (config/restore)
        history (io/file (:history config))]
    (if (.exists history)
      (edn/read-string (slurp history)))))

(defn record
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - wallpaper (String): Add the wallpaper to the history of displayed papers."
  [wallpaper]
  (let [config (config/restore)
        wallpapers (restore)]
    (spit (:history config) (pr-str (cons wallpaper wallpapers)))))

(defn dump
  "Print the contents of the history to STDOUT."
  []
  (let [config (config/restore)]
    (pprint (edn/read-string (slurp (:history config))))))

(defn clear
  "Clear the history contents to start over."
  []
  (let [config (config/restore)
        history (io/file (:history config))]
    (if (.exists history)
      (.delete history))))

(defn set-current
  "Record the given wallpaper as the current.

  Arguments:
  - wallpaper (String): String path of the current wallpaper to save"
  [wallpaper]
  (let [config (config/restore)]
    (spit (:current config) (pr-str wallpaper))))

(defn set-previous
  "Sets the previous wallpaper to the current"
  []
  (let [config (config/restore)
        current (io/file (:current config))
        previous (io/file (:previous config))]
    (io/copy current previous)))

(defn get-previous
  "Gets the previous wallpaper."
  []
  (let [config (config/restore)]
    (edn/read-string (slurp (:previous config)))))
