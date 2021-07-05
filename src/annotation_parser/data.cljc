(ns annotation-parser.data
  (:require [datascript.core :as d]
            [datascript.db :as db]))

(def book-schema {:book/author {:db/type :db.type/ref}
                  :book/title {:db/type :db.type/string
                               :db/unique :db.unique/identity}
                  :book/year-published {:db/type :db.type/long}
                  ;; :book/date-read
                  ;; :book/passages {:db/cardinality :db.cardinality/many
                  ;;                :db/type :db.type/ref}}
                  })
(def passage-schema {:passage/book {:db/type :db.type/ref}
                     :passage/text {:db/type :db.type/string}
                     :passage/date {:db/type :db.type/string}
                     :passage/annotations {:db/cardinality :db.cardinality/many
                                           :db/type :db.type/string}})
(def author-schema {:author/name {:db/type :db.type/string}})


(def schema (merge book-schema passage-schema author-schema))

(def conn (d/create-conn schema))

(defn book-transaction
  ""
  [book]
  (vector
   {;;:book/author (:title book)
    :book/title (:title book)
    :book/year-published 0}
    ))
;; Just transact passages? use input for title + author
;; Title should be introduced from input
(defn passage-transaction
  ""
  [book]
  (map (fn [passage] {:passage/book [:book/title (:title book)]
           :passage/text (:text passage)
           :passage/date (:date passage)
           :passage/annotations (:annotations passage)}) (:passages book)))

(defn init
  "Get da books into da datah base"
  [books]
  (map commit-all books))


(comment (d/transact! conn [{:db/id "jw"
                    :author/name "John Williams"}
                   {:book/author "jw"
                    :db/id "stoner"
                    :book/title "Stoner"
                    :book/year-published 1965}
                   {:passage/book "stoner"
                    :passage/annotations ["testie" "graub"]}]))
