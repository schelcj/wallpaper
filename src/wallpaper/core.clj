;; TODO
;; - write weight function in papers namespace
;; - write function to set the paper to the previous images
;; - write function to set to a specific image, bypassing all filtering
;; - write funciton to tile the wallpaper instead of setting fullscreen
;; - cleanup all params and docs (not sure the comments are correct and that i'm using the correct type)
;; - sources and categories is confusing, sort it out
;; - write all the tests
;; - update project, readme, and changelog
;; - fix any errors that `lein run` uncovers
;; - add config file support
;; - change prefix to use `XDG_CONFIG_HOME` or fallback to `~/.config/wallpapers`
;; - make the setter configurable
(ns wallpaper.core
  (:require [wallpaper.history :as history])
  (:require [wallpaper.category :as category])
  (:require [wallpaper.papers :as papers])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :refer [sh]])
  (:gen-class))

(def prefix (io/file (System/getenv "HOME") ".wallpapers"))

(def config {:lock-file (io/file prefix "lock")
             :category-file (io/file prefix "category.edn")
             :wallpapers-dir (io/file prefix "Wallpapers")
             :current (io/file prefix "current.edn")
             :previous (io/file prefix "previous.edn")
             :history (io/file prefix "history.edn")
             :sources (io/file prefix "sources")
             :default-category "all"})

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

(defn set-wallpaper
  "Actually set the wallpaper."
  []
  (let [
    sources (category/all (:sources config))
    dirs (papers/dirs (:wallpapers-dir config) sources)
    wallpapers (papers/gather dirs)
    filtered-wallpapers (papers/prune (:history config) wallpapers)
    weighted-wallpapers (papers/weight filtered-wallpapers)
    new-wallpaper (papers/random weighted-wallpapers)
    ]
    (println (str "fbsetbg -f " new-wallpaper))
    (sh "fbsetbg" "-f" new-wallpaper)
    (history/set-previous (:current config) (:previous config))
    (history/set-current (:current config) new-wallpaper)
    (history/record (:history config) new-wallpaper)))

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
        (do (.delete (io/file (:lock-file config))))
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
          ;; TODO
          (println "set the wallpaper to the contents of the previous file"))
      (:image options)
        (do
          ;; TODO
          (println "set the wallpaper to the given image file"))
      (:tile options)
        (do
          ;; TODO
          (println "set the wallpaper to the given image tiled"))
      (:clear options)
        (do
          (category/clear (:category-file config))))
    (set-wallpaper))
    (System/exit 0))
