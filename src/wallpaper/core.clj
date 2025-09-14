;; TODO
;; - write funciton to tile the wallpaper instead of setting fullscreen
;; - add an `--init` option to setup the configuration files and directory
;; - not following naming conventions for functions with side-effects, should rename things
;; - write all the tests
;; - unknown args are silently ignored, probably should throw an error
;; - missing required values for args are not throwing anything (e.g. `--tile` without image)
;;
;; - cleanup all params and docs (not sure the comments are correct and that i'm using the correct type)
;; - update project, readme, and changelog
;; - experiment with github actions to run tests and builds for binary downloads
(ns wallpaper.core
  (:require [wallpaper.history :as history])
  (:require [wallpaper.config :as config])
  (:require [wallpaper.category :as category])
  (:require [wallpaper.papers :as papers])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io])
  (:gen-class))

(def cli-options
  [["-c" "--category CATEGORY" "Wallpaper category"]
   ["-f" "--flush-cache" "Flush the wallpaper history cache"]
   ["-d" "--dump-cache" "Print current wallpaper history cache to STDOUT"]
   ["-l" "--lock" "Lock the current wallpaper"]
   ["-u" "--unlock" "Unlock the current wallpaper"]
   ["-p" "--previous" "Set the wallpaper to the previous image"]
   ["-i" "--image IMAGE" "Set the provided image as the current wallpaper"]
   ["-t" "--tile TILE" "Tiled the provided image as the wallpaper"]
   ["-r" "--clear" "Clear the previous wallpaper category"]
   ["-h" "--help" "Show help"]])

(defn usage [options-summary]
  (->> ["wallpaper"
        ""
        "Usage: wallpaper [options]"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn -main
  [& args]
  (let [config (config/restore)
        {:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (do
        (println (usage summary))
        (System/exit 0))
      (:lock options)
      (do
        (spit (:lock-file config) "")
        (System/exit 0))
      (:unlock options)
      (do
        (.delete (io/file (:lock-file config))))
      (:category options)
      (do
        (category/record (:category options)))
      (:dump-cache options)
      (do
        (history/dump)
        (System/exit 0))
      (:flush-cache options)
      (do
        (history/clear)
        (System/exit 0))
      (:previous options)
      (do
        (papers/display (history/get-previous))
        (System/exit 0))
      (:image options)
      (do
        (papers/display (:image options))
        (System/exit 0))
      (:tile options)
      (do
        ;; TODO
        (println "set the wallpaper to the given image tiled")
        (System/exit 0))
      (:clear options)
      (do
        (category/clear)
        (System/exit 0)))

    (let [lock (io/file (:lock-file config))]
      (if (.exists lock)
        (System/exit 1)
        (papers/display (papers/random)))))

  (System/exit 0))
