;; TODO
;; - single category selection is not working
;; - pass around the config map or not, not sure what the best approach is?
;;   - maybe a config namespace to house all that stuffs...
;; - not following naming conventions for functions with side-effects, should rename things
;; - write funciton to tile the wallpaper instead of setting fullscreen
;; - cleanup all params and docs (not sure the comments are correct and that i'm using the correct type)
;; - sources and categories is confusing, sort it out
;; - write all the tests
;; - update project, readme, and changelog
;; - add config file support
;; - change prefix to use `XDG_CONFIG_HOME` or fallback to `~/.config/wallpapers`
;; - make the setter configurable
;; - experiment with github actions to run tests and builds for binary downloads
;; - unknown args are silently ignore, probably should throw an error
(ns wallpaper.core
  (:require [wallpaper.history :as history])
  (:require [wallpaper.category :as category])
  (:require [wallpaper.papers :as papers])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io])
  (:gen-class))

(def prefix (io/file (System/getenv "HOME") ".wallpapers"))

(def config {:lock-file (io/file prefix "lock")
             :category-file (io/file prefix "category.edn")
             :wallpapers-dir (io/file prefix "Wallpapers")
             :current (io/file prefix "current.edn")
             :previous (io/file prefix "previous.edn")
             :history (io/file prefix "history.edn")
             :sources (io/file prefix "sources")
             :default-category "all"
             :weights {86400 1000
                       604800 500
                       2592000 200}})

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
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
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
        (category/record (:category-file config) (:category options)))
      (:dump-cache options)
      (do
        (history/dump (:history config))
        (System/exit 0))
      (:flush-cache options)
      (do
        (history/clear (:history config))
        (System/exit 0))
      (:previous options)
      (do
        (papers/display config (history/get-previous (:previous config)))
        (System/exit 0))
      (:image options)
      (do
        (papers/display config (:image options))
        (System/exit 0))
      (:tile options)
      (do
        ;; TODO
        (println "set the wallpaper to the given image tiled"))
      (:clear options)
      (do
        (category/clear (:category-file config))))

      (papers/display config (papers/random config)))
  (System/exit 0))
