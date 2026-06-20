(ns wallpaper.category
  "Functions to handle filtering the search for available wallpapers in categories (i.e. directories)."
  (:require [wallpaper.config :as cfg])
  (:require [clojure.java.io :as io])
  (:require [clojure.edn :as edn])
  (:gen-class))

(defn all!
  "Returns all the categories to search for wallpapers as defined in the `:categories`
  config value or from the category lock file set by `--category`."
  []
  (let [category-file (io/file (:category-file cfg/config))]
    (if (.exists category-file)
      (edn/read-string (slurp category-file))
      (:categories cfg/config))))

(defn record!
  "Record the category to filter to the `category.edn` file within the configuration directory.
  This will limit the selection of wallpapers to just images within the directory.

  Arguments:
  - category (str): Category name (i.e. the directory) to limit selection to."
  [category]
  (spit (:category-file cfg/config) (pr-str [category])))

(defn clear!
  "Delete the category lock file used to filter the search for available wallpapers."
  []
  (.delete (io/file (:category-file cfg/config))))
