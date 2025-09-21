;; TODO
;; - not following naming conventions for functions with side-effects, should rename things
;; - write all the tests
;; - make the setter configurable to switch out `fbsetbg`
;;
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
   ["-a" "--add-category CATEGORY" "Add category to the selection list"]
   ["-d" "--del-category CATEGORY" "Remove category from the selection list"]
   ["-f" "--flush-cache" "Flush the wallpaper history cache"]
   ["-D" "--dump-cache" "Print current wallpaper history cache to STDOUT"]
   ["-l" "--lock" "Lock the current wallpaper"]
   ["-u" "--unlock" "Unlock the current wallpaper"]
   ["-p" "--previous" "Set the wallpaper to the previous image"]
   ["-i" "--image IMAGE" "Set the provided image as the current wallpaper"]
   ["-t" "--tile TILE" "Tiled the provided image as the wallpaper"]
   ["-r" "--clear" "Clear the previous wallpaper category"]
   ["-I" "--init" "Initialize caching and configuration files"]
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
      (seq errors)
      (do
        (println errors)
        (println)
        (println (usage summary))
        (System/exit 1))
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
      (:add-category options)
      (do
        (category/add-category! (:add-category options))
        (System/exit 0))
      (:del-category options)
      (do
        (category/del-category! (:del-category options))
        (System/exit 0))
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
        (let [wallpaper (history/get-previous)]
          (papers/display-fullscreen wallpaper))
        (System/exit 0))
      (:image options)
      (do
        (let [wallpaper (:image options)]
          (papers/display-fullscreen wallpaper))
        (System/exit 0))
      (:tile options)
      (do
        (let [wallpaper (:tile options)]
          (papers/display-tiled wallpaper))
        (System/exit 0))
      (:clear options)
      (do
        (category/clear)
        (System/exit 0))
      (:init options)
      (do
        (config/init!)
        (println "Initialization complete now set the wallpaper path in the config file:")
        (println "Default configuration path: " (config/default-config-path))
        (System/exit 0)))

    (let [lock (io/file (:lock-file config))]
      (if (.exists lock)
        (System/exit 1)
        (let [wallpaper (papers/random)]
          (papers/display-fullscreen wallpaper)
          (papers/record wallpaper)))))

  (System/exit 0))
