(ns annotation-parser.byreader
  (:require [annotation-parser.core :as apcore]
            [tupelo.parse.tagsoup :as ts]))
;; For testing
(def test-file "stoner-test.html")
(def test (ts/parse (slurp test-file)))
;; BYREADER NOTES
;; No location information is provided for marked passages
;; Can verify its BYReader by the Chinese string at the end of file
;;
;; Each passage is nested in a div with :style 'padding-top: 1em; padding-bottom: 1em; '
;;

;; Separate soup into book title, passages, annotations
(defn get-title
  "Output book title as string"
  [soup]
  (->> soup
       (get-body) (:content) (first) (:content) (first) (:content) (first) (:content) (first)))

;; Passage functions
;; NOTE: (get-passages [soup]) returns the :div with passages nested
(defn get-passage-date
  "Output date of first passage in div"
  [passage-div]
  (->> passage-div
       (first) (:content) (first) (:content) (first)))
(defn get-passage-text
  "Output first passage text as string (use recursive rest calls to pull all passages?)"
  [passage-div]
  (->> passage-div
       (first) (:content) (second) (:content) (first)))
(defn get-passage-annotations
  "Output first (and only) annotation into array. Returns empty array if empty-annotation? returns true"
  [passage-div]
  (->> passage-div
       (first) (:content) (#(second (next %))) (:content) (first) (:content) (first) (:content) (second) (:content) (first)))

;; Verification functions
(defn empty-annotation?
  "Checks if the text of annotation is 'Underline notes'"
  [annotation]
  (= "Underline notes" annotation))

;; Helper functions that combine title, passages, annotations into map
;; {:title "title"
;;     :passages [{:date "date", :text "passage text", :annotations ["first annotation"]}
;;      {:date "date", :text "second passage", :annotations ["first annotation"]}
;; }
(defn put-title
  "Puts title into map, returns map"
  [title book-map])
(defn put-passage
  "Attaches passage, annotations, and date into map with title, returns map"
  [passage book-map])

;; The big cheese; the mondo function; the One
(defn put-book
  "Assemble complete map using put-title and put-passage"
  [soup])

;; Parsing navigation helpers
(defn get-body
  "Map with :tag ':body'"
  [soup]
  (->> soup
       (:content) (second)))
(defn get-passages
  "Map with :tag ':div' that corresponds to passage nest"
  [soup]
  (->> soup
       (get-body) (:content) (first) (:content) (first) (:content) (#(drop 3 %))))
