(ns annotation-parser.byreader
  (:require [annotation-parser.core :as apcore]
            [tupelo.parse.tagsoup :as ts]
            [tupelo.core :as tupelo]))
;; For testing
(def test-file "stoner-test.html")
(def test (ts/parse (slurp test-file)))
(def full-test "stoner-test-full.html")
(def full-test (ts/parse (slurp full-test)))

;; Declarations
(declare get-body get-passages)
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
       (first) (:content) (#(second (next %))) (:content) (first) (:content) (first) (:content) (second) (:content) (first)
       (vector)))
;; Verification functions
(defn empty-annotation?
  "Checks if the text of annotation is 'Underline notes'"
  [annotation]
  (= "Underline notes" annotation))
(defn last-passage?
  "Checks if given passage is the last in the list"
  [passage-list]
  (= nil (get-passage-date passage-list)))

;; Functions that build the following data structure
;; {:title "title"
;;     :passages [{:date "date", :text "passage text", :annotations ["first annotation"]}
;;      {:date "date", :text "second passage", :annotations ["first annotation"]}]
;; }
(defn put-title
  "Puts title into map, returns map. First function called to initialize book map."
  [title]
  {:title title})
(defn assemble-passage
  "Attaches text, annotations, and date into map."
  [passage-list]
  {:date (get-passage-date passage-list), :text (get-passage-text passage-list),
   :annotations (get-passage-annotations passage-list)})
(defn put-passages
  "Recursively builds the array of passages from put-passage. Uses apply to avoid nesting.
  Final assembled passage is enclosed in array to avoid discontinuity at final apply call."
  [passage-list]
  (if (last-passage? passage-list) [(assemble-passage passage-list)]
      (apply vector (assemble-passage passage-list) (put-passages (rest passage-list)))))

;; The big cheese; the mondo function; the One
(defn put-book
  "Assemble complete map using put-title and put-passages"
  [soup]
  (conj (put-title (get-title soup)) {:passages (put-passages (get-passages soup))}))

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
