(ns annotation-parser.core
  (:require [tupelo.parse.tagsoup :as ts]
            [annotation-parser.readers.byreader :as by]))

(def test-file "stoner-test.html")
(def full-test-file "stoner-test-full.html")
(def batch ["test-files/batch/1.html" "test-files/batch/2.html" "test-files/batch/3.html" "test-files/batch/4.html" "test-files/batch/5.html"])

(declare byreader?)

;; Read file and determine type (type determinations are at EoF in prep for more ereader formats)
(defn html-read
  "Read file, output tagsoup (nested maps)"
  [file]
  (ts/parse (slurp file)))

;; Send to appropriate NS
(defn ns-call
  "Call put-book for appropriate reader"
  [file]
  (cond
    (byreader?) (by/put-book (html-read file))))

;; run ns-call on each book in batch, building list of maps
(defn batch-call
  [files]
  (map ns-call files))

;; Type checkers
(defn byreader?
  "Check if file is from BYReader"
  []
  true)
