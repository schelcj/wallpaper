(ns wallpaper.history
  "Functions for history cache handling."
  (:require [wallpaper.config :as cfg])
  (:require [clojure.edn :as edn])
  (:require  [clojure.java.io :as io])
  (:gen-class))

(defn restore!
  "Read the history of previously displayed wallpapers."
  []
  (let [history (io/file (:history cfg/config))]
    (if (.exists history)
      (edn/read-string (slurp history)))))

(defn record!
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - wallpaper (String): Add the wallpaper to the history of displayed papers."
  [wallpaper]
  (let [wallpapers (restore!)]
    (spit (:history cfg/config) (pr-str (cons wallpaper wallpapers)))))

(defn clear!
  "Clear the history contents to start over."
  []
  (spit (:history cfg/config) ()))

(defn set-current!
  "Record the given wallpaper as the current.

  Arguments:
  - wallpaper (String): String path of the current wallpaper to save"
  [wallpaper]
  (spit (:current cfg/config) (pr-str wallpaper)))

(defn get-current!
  "Gets the current wallpaper"
  []
  (edn/read-string (slurp (:current cfg/config))))

(defn set-previous!
  "Sets the previous wallpaper to the current"
  []
  (let [current (io/file (:current cfg/config))
        previous (io/file (:previous cfg/config))]
    (io/copy current previous)))

(defn get-previous!
  "Gets the previous wallpaper."
  []
  (edn/read-string (slurp (:previous cfg/config))))

(defn get-relative-path
  "Gets a wallpapers path relative to the base directory (i.e. the category)."
  [path]
  (subs path (inc (count (:wallpapers-dir cfg/config)))))
