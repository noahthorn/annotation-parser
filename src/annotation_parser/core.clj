(ns annotation-parser.core
  (:require [tupelo.parse.tagsoup :as ts]
            [annotation-parser.byreader :as by]))

(def test-file "stoner-test.html")
(def full-test-file "stoner-test-full.html")

;; Read file and determine type
(defn html-read
  "Read file, output tagsoup (nested maps)"
  [file])
(defn type?
  "Determine e-reader source by reading tagsoup"
  [soup])

;; Send to appropriate NS
(defn ns-call
  "Call put-book for appropriate reader"
  [source-type file])

;; Receive from appropriate NS and output as JSON:API data
;; Will probably be a call to another namespace
