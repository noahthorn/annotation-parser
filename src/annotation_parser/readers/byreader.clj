(ns annotation-parser.readers.byreader
  (:require [annotation-parser.core :as apcore]
            [tupelo.parse.tagsoup :as ts]
            [tupelo.core :as tupelo]))
;; For testing
(def test-file "test-files/stoner-test.html")
(def test-soup (ts/parse (slurp test-file)))


;; Declarations
(declare get-body get-passages empty-annotation?)
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
  (let [annotation (->> passage-div
       (first) (:content) (#(second (next %))) (:content) (first) (:content) (first) (:content) (second) (:content) (first)
       (vector))]
      (if (empty-annotation? (first annotation)) [] annotation)))
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
  "Constructs a vector containing maps for each marked passage, with date, text, and annotation(s)"
  [passage-list]
  (loop [result [] pl passage-list]
    (if (last-passage? pl) result
        (recur (conj result (assemble-passage pl)) (rest pl)))))

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
