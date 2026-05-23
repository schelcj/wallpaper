(ns wallpaper.fileutils
  "Collection of utilities for dealing with filenames similiar to unix command line utilities.
  Losely based around the perl module File::Basename."
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as s]))

(defn basename
  "Compatible with the unix shell utility `basename`. Returns the last level
  of a filepath regardless if that is a file or directory.

  Examples:
  - /some/path/foo.pdf => foo.pdf
  - /some/path/        => path

  Arguments:
  - path (string): path to file or directory"
  [path]
  (let [separator (System/getProperty "file.separator")
        components (.split (re-pattern separator) path)]
    (nth (take-last 1 components) 0)))

(defn dirname
  "Compatible with the unix shell utility `dirname`. Returns the last non-slash
  component of the path. If the file is in the current directory then `.` is returned.

  Examples:
  - /some/path/foo.pdf => /some/path
  - foo.pdf            => .

  Arguments:
  - path (string): path to strip down to the last component (file or directory)"
  [path]
  (let [separator (System/getProperty "file.separator")
        components (.split (re-pattern separator) path)
        dirname (s/join separator (drop-last components))]
    (if (empty? dirname)
      "."
      dirname)))

(defn fileparse
  "Parse a file path into directory, filename, and extension. Returns a map of
  `:dirname`, `:filename`, and `:ext`. Filename will be the name without the
  file extension.

  Examples:
  - /var/tmp/foo.pdf => {:dirname '/var/tmp', :filename 'foo', :ext 'pdf'}

  Arguments:
  - path (string): path to file"
  [path]
  (let [filename (basename path)
        ext (last (.split (re-pattern #"\.") path))
        name (last (.split (re-pattern (str "." ext)) filename))]
  {:dirname (dirname path)
   :filename name
   :ext ext}))

(defn add-file-suffix
  "Create a new file path with a given suffix appended to the name. Returns
  a string path to a new filename.

  Examples:
  - /some/path/foo.jpeg with new suffix `gray` => /some/path/foo_gray.jpeg
  - /some/path/foo.jpeg with new suffix `gray` and dirname `/tmp` => /tmp/foo_gray.jpeg

  Arguments:
  - path (string): path to file
  - suffix (string): string to append to the filename
  - dir: (string): optional directory name to replace existing dirname value [OPTIONAL]"
  [path suffix & [dir]]
  (let [separator (System/getProperty "file.separator")
        fileinfo (fileparse path)
        name (format "%s_%s.%s" (:filename fileinfo) suffix (:ext fileinfo))
        dirname (or dir (:dirname fileinfo))]
    (s/join separator [dirname name])))
