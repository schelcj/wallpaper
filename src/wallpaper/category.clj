(ns wallpaper.category
  "Functions to handle filtering the search for available wallpapers in categories (i.e. directories)."
  (:require [wallpaper.config :as config])
  (:require [clojure.java.io :as io])
  (:require [clojure.edn :as edn])
  (:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn all!
  "Returns all the category (i.e. directories) to search for wallpapers.
  The default categories are in the `sources.edn` file within the configuration
  directory. These are returned by default but can be overriden with the `--category`
  argument which will limit selection to just that directory. This is stored in the
  `category.end` file within the configuration directory."
  []
  (let [config (config/restore!)
        category-file (io/file (:category-file config))]
    (if (.exists category-file)
      (edn/read-string (slurp category-file))
      (edn/read-string (slurp (:sources config))))))

(defn record!
  "Record the category to filter to the `category.edn` file within the configuration directory.
  This will limit the selection of wallpapers to just images within the directory.

  Arguments:
  - category (str): Category name (i.e. the directory) to limit selection to."
  [category]
  (let [config (config/restore!)]
    (spit (:category-file config) (pr-str [category]))))

(defn clear!
  "Delete the category file that is used to filter the search for available wallpapers.
  This will allow selection of all wallpapers defined in the `sources.edn` file."
  []
  (let [config (config/restore!)]
    (.delete (io/file (:category-file config)))))

(defn add-category!
  "Adds a category to the list of categories search for a random wallpaper.

  Arguments:
  - category (String): category to add"
  [category]
  (let [config (config/restore!)
        sources (edn/read-string (slurp (:sources config)))]
    (spit (:sources config) (seq (conj (set sources) category)))))

(defn del-category!
  "Removes a category from the list of categories to search for a random wallpaper.

  Argurments:
  - category (String): category to remove"
  [category]
  (let [config (config/restore!)
        sources (edn/read-string (slurp (:sources config)))]
    (spit (:sources config) (pr-str (filter #(not= category %) sources)))))

(defn show-categories!
  "Show the currently configured sources that will be used in random selection."
  []
  (let [config (config/restore!)]
    (pprint (edn/read-string (slurp (:sources config))))))
