(ns annotation-parser.html
  (:require [hiccup.core :as hiccup]))

;; Extract components of annotations
;; :title :passages>[:date :text :annotations]
(defn convert-title
  "takes book + outputs title as h1"
  [book]
  (hiccup/html [:h1 (:title book)]))

(defn extract-passages
  [book]
  (:passages book))

(defn annotation?
  [passage]
  (not (empty? (:annotations passage))))

(defn convert-passage
  "takes passage + outputs as div with component parts"
  [passage]
  (hiccup/html [:div {:class "passage"}
                [:span {:class "date"} (:date passage)]
                [:span {:class "passage-text"} (:text passage)]
                (cond
                  (annotation? passage) [:span {:class "annotations"} (first (:annotations passage))])]))

(defn passage-loop
  [passages]
  (map convert-passage passages))

(defn build-book
  [book]
  (hiccup/html [:div {:class "book"}
                (convert-title book)
                [:div {:class "passages"}
                 (passage-loop (extract-passages book))]]))

;; (defn )

(defn build-books
  "outputs array of DIVs(strings), each representing one book"
  [books]
  (map build-book books))

;; build page!
