(ns gilded-rose.core
  (:require [clara.rules :refer :all]))


;; Records

(defrecord Item [name sell-in quality type])

(defrecord AgedItem [item])

(defrecord QualityAssurance [item])


;; Rules

(defrule age-items-still-in-sell-by
  "Age an item not passed is sell-in day."
  [?item <- Item
   (< 0 sell-in)
   (= :normal type)]

  =>
  (insert! (->AgedItem (-> ?item
                           (update :sell-in dec)
                           (update :quality dec)))))


(defrule passed-sell-by-quality-degrades-faster
  "Once the sell by date has passed, Quality degrades twice as fast."
  [?item <- Item
   (<= sell-in 0)]
  =>
  (insert! (->AgedItem (-> ?item
                           (update :sell-in dec)
                           (update :quality - 2)))))


(defrule aged-brie-improves-with-age
  "Aged Brie actually increases in quality the older it gets."
  [?item <- Item
   (= "Aged Brie" name)]
  =>
  (insert! (->AgedItem (-> ?item
                           (update :sell-in dec)
                           (update :quality inc)))))


(defrule sulfuras-legendary-item
  "Sulfuras, being a legendary item, never has to be sold or decreases in
  quality."
  [?item <- Item
   (= "Sulfuras" name)]
  =>
  (insert! (->AgedItem ?item)))


(defrule quality-from-zero-check
  "The Quality of an item is never negative."
  [?item <- AgedItem [{{quality :quality} :item}]
   (= ?quality quality)]
  [:test (<= 0 ?quality 50)]
  =>
  (insert! (->QualityAssurance (:item ?item))))


(defrule quality-cannot-be-less-than-zero
  "The Quality of an item is never negative."
  [?item <- AgedItem [{{quality :quality} :item}]
   (= ?quality quality)]
  [:test (< ?quality 0)]
  =>
  (insert! (->QualityAssurance (assoc (:item ?item) :quality 0))))


(defrule quality-cannot-be-greater-than-fifty
  "The Quality of an item is never more than 50."
  [?item <- AgedItem [{{quality :quality} :item}]
   (= ?quality quality)]
  [:test (< 50 ?quality)]
  =>
  (insert! (->QualityAssurance (assoc (:item ?item) :quality 50))))


;; Queries

(defquery completed-items
  "Query to find all completely aged items."
  []
  [?item <- QualityAssurance])
