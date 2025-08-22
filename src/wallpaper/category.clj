(ns wallpaper.category
  "Functions to handle filtering the search for available wallpapers in categories (i.e. directories)."
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn all
  "Returns all the category (i.e. directories) to search for wallpapers.

  Arguments:
  - file (File): Location of the category file.
  - category (String): Optional category name to limit the search to a single directory.
  "
  [file & category]
  (if category
    (vec category)
    (with-open [r (io/reader file)]
      (vec (line-seq r)))))

(defn set
  "Record the category to filter to.

  Arguments:
  - file (File): Location of the category file.
  - category (str): Category name (i.e. the directory)."
  [file & category]
  (spit file (pr-str category)))

(defn clear
  "Delete the category file that is used to filter the search for available wallpapers.

  Arguments:
  - file (File): Location of the category file."
  [file]
  (.delete (io/file file)))
