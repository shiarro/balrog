(ns balrog.utilities-test
  (:use clojure.test
        balrog.utilities))


(deftest randint-range-test
  (testing "Random integer ranges"
     (are [n]
        (let [r (randint n)] (and (>= r 1) (<= r n)))
        1 1 1 1 1 1 1 1 1 1
        10 10 10 10 10 10 10 10 10 10)))


(deftest new-randint-dist-test
  (testing "Random integer distribution"
     (let [v (vals (frequencies (for [_ (range 500)] (randint 10))))]
       (is (every? #(< % 100) v))
       (is (every? #(> % 25) v)))))
  

(deftest remove-nth-test
  (testing "Remove-nth"
     (are [l n r]
        (= (remove-nth l n) r)
        '(1 2 3) 0 '(2 3)
        '(1 2 3) 1 '(1 3)
        '(1 2 3) 2 '(1 2)
        '(1 2 3) 3 '(1 2 3)
        '(1 2 3) -1 '(1 2 3))))

