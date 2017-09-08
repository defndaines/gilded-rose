(ns gilded-rose.core-test
  (:require [clojure.test :refer :all]
            [clara.rules :as rules]
            [gilded-rose.core :refer :all]))


(deftest age-item-passed-sell-by-test
  (testing "Items passed their sell-by will age twice as fast."
    (let [session (-> (rules/mk-session 'gilded-rose.core)
                      (rules/insert (->Item "Doohickey" -1 5))
                      (rules/fire-rules))
          results (rules/query session completed-items)]
      (is (= 1
             (count results)))
      (is (= 3
             (-> (first results)
                 (get-in [:?item :item :quality])))))))
