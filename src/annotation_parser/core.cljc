(ns annotation-parser.core
  (:require [tupelo.parse.tagsoup :as ts]
            [clojure.string :as str]
            [annotation-parser.readers.byreader :as by]
            [annotation-parser.html :as build]
            [annotation-parser.data :as ds]))

(def test-file "test-files/stoner-test.html")
(def full-test-file "test-files/stoner-test-full.html")
(def batch ["test-files/batch/1.html" "test-files/batch/2.html" "test-files/batch/3.html" "test-files/batch/4.html" "test-files/batch/5.html"])

(declare byreader?)

;; Send to appropriate NS
(defn ns-call
  "Call put-book for appropriate reader"
  [file]
  (cond
    (byreader? file) (by/pre-put-book file)))

;; run ns-call on each book in batch, building list of maps
(defn batch-call
  [files]
  (map ns-call files))

(defn load-in-db
  [books]
  (ds/init books))

;; ANTIQUATED -- now using DB
;; START output
;; call html builder
(defn html-output
  [books]
  (build/build-books books))

(defn main
  ""
  [html-files]
  (html-output (batch-call html-files)))

;; Type checkers
(defn byreader?
  "Check if file is from BYReader"
  [file]
  true)
