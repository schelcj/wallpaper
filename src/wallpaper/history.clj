(ns wallpaper.history
  "Functions for history cache handling."
  (:require [clojure.edn :as edn])
  (:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn load
  "Read the history of previously displayed wallpapers.

  Arguments:
  - history (File): Location of the history cache."
  [history]
  (edn/read-string (slurp history)))

(defn record
  "Save the history of wallpapers that have been used to disk to avoid displaying the same wallpaper repeatedly.

  Arguments:
  - history (File): Location of the history cache.
  - wallpapers (vector): Wallpapers that have been displayed to be recorded for subsequent runs."
  [history wallpapers]
  (spit history (pr-str wallpapers)))

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
