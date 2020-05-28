(ns annotation-parser.core
  (:require [tupelo.parse.tagsoup :as ts]
            [annotation-parser.byreader :as by]))

(def test-file "stoner-test.html")
(def full-test-file "stoner-test-full.html")

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

;; Type checkers
(defn byreader?
  "Check if file is from BYReader"
  []
  true)
