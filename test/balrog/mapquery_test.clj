(ns balrog.mapquery-test
    (:use clojure.test
          balrog.mapquery))


(deftest mapobj-contains-test
  (testing "Point inside and outside boundaries of 1 object"
     (let [thing {:x 5 :y 5 :w 5 :h 5}]
       (is (mapobj-contains? thing 5 5))
       (is (mapobj-contains? thing 5 9))
       (is (mapobj-contains? thing 9 5))
       (is (mapobj-contains? thing 9 9))
       (is (not (mapobj-contains? thing 4 5)))
       (is (not (mapobj-contains? thing 5 4)))
       (is (not (mapobj-contains? thing 9 10)))
       (is (not (mapobj-contains? thing 10 9))))))
  

(deftest any-mapobj-contains-test
  (testing "Points inside and outside boundaries of a few objects"
     (let [stuff '({:x 5 :y 5 :w 5 :h 5}
                   {:x 10 :y 10}
                   {:x 0 :y 0 :w 1 :h 24})]
       (is (any-mapobj-contains? stuff 6 6))
       (is (any-mapobj-contains? stuff 10 10))
       (is (any-mapobj-contains? stuff 0 4))
       (is (not (any-mapobj-contains? stuff 1 4)))
       (is (not (any-mapobj-contains? stuff 10 11)))
       (is (not (any-mapobj-contains? stuff 5 4))))))
