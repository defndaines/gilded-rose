(ns gilded-rose.core-test
  (:require [clojure.test :refer :all]
            [clara.rules :as rules]
            [gilded-rose.core :refer :all]))


(defn- get-item
  "Pull the item out of a result."
  [result]
  (get-in result [:?item :item]))


(deftest age-normal-item-test
  (testing "Item quality decreases with age."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Doohickey" 3 5 :normal))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 4 :sell-in 2}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in]))))))

  (testing "Items passed their sell-by will degrade in quality twice as fast."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Doohickey" 0 5 :normal))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 3 :sell-in -1}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in]))))))

  (testing "Quality of an item is never negative."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Doohickey" 1 0 :normal))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 0 :sell-in 0}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in]))))))

  (testing "Quality of an item is never greater than 50."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Doohickey" 1 60 :normal))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 50 :sell-in 0}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in])))))))

(deftest aged-brie-test
  (testing "Aged Brie actually increases in quality the older it gets."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Aged Brie" 1 10 :special))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 11 :sell-in 0}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in]))))))

  (testing "Maximum quality still applies to Aged Brie."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Aged Brie" 1 50 :special))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= {:quality 50 :sell-in 0}
             (-> (first results)
                 get-item
                 (select-keys [:quality :sell-in])))))))

(deftest sulfuras-test
  (testing "Sulfuras, being a legendary item, never has to be sold or
           decreases in quality.")
  (let [session (-> (rules/mk-session 'gilded-rose.core)
                    (rules/insert (->Item "Sulfuras" 1 50 :special))
                    (rules/fire-rules))
        results (rules/query session completed-items)]
    (is (= {:quality 50 :sell-in 1}
           (-> (first results)
               get-item
               (select-keys [:quality :sell-in]))))))
