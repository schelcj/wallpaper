(ns wallpaper.history
  "Functions for history cache handling."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn restore
  "Read the history of previously displayed wallpapers.

  Arguments:
  - history (File): Location of the history cache."
  [history]
  (edn/read-string (slurp history)))

(defn record
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - history (File): Location of the history cache.
  - wallpaper (String): Add the wallpaper to the history of displayed papers."
  [history wallpaper]
  (let [wallpapers (restore history)]
    (spit history (pr-str (cons wallpaper wallpapers)))))

(defn dump
  "Print the contents of the history to STDOUT.

  Arguments:
  - history (File): Location of the history cache."
  [history]
  (pprint (edn/read-string (slurp history))))

(defn clear
  "Clear the history contents to start over.

  Arguments:
  - history (File): Location of the history cache."
  [history]
  (record history []))

(defn set-current
  "Record the given wallpaper as the current.

  Arguments:
  - file (File): Location of the current file
  - wallpaper (String): String path of the current wallpaper to save"
  [current wallpaper]
  (spit current (pr-str wallpaper)))

(defn set-previous
  "Sets the previous wallpaper to the current

  Arguments:
  - current (File): Location to the current file
  - previous (File): Location of the previous file"
  [current previous]
  (io/copy current previous))

(defn get-previous
  "Gets the previous wallpaper.

  Arguments:
  - previous (String): Path to the previous file"
  [previous]
  (edn/read-string (slurp previous)))
