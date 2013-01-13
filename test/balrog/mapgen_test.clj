(ns balrog.mapgen-test
  (:use clojure.test
        balrog.mapgen))


(deftest get-default-map-config-test
  (testing "Default map configuration retrieval"
     (is (map? (get-default-map-config)))))


(deftest default-map-config-contents-test
  (testing "Contents of default map config"
    (let [mc (get-default-map-config)]
      (is (map? (mc :town))))))
; TODO: Add other top-level map config stuff here


(deftest default-town-config-contents-test
  (testing "Contents of default town config"
    (let [mc (get-default-map-config)]
      (is (map? (-> mc :town :stores)))
      (is (vector? (-> mc :town :stores :types)))
      (is (> (count (-> mc :town :stores :types)) 0)) 
      (is (map? (-> mc :town :stores :grid)))
      (is (contains? (-> mc :town :stores :grid) :x))
      (is (contains? (-> mc :town :stores :grid) :y)))))       
           

(deftest gen-store-test
  (testing "Store generation for town level"
     (dotimes [_ 100] ; Since it has randomization, run it several times 
       (let [s (gen-store :dummy-type 0 0)]
         (is (s :objtype) :store)
         (is (s :type) :dummy-type)
         (is (> (-> s :x) 0))
         (is (> (-> s :y) 0))
         (is (> (-> s :w) 0))
         (is (> (-> s :h) 0))
         (is (>= (-> s :door :x) (-> s :x))
             "Door is right of the store's left wall")
         (is (<= (-> s :door :x) (dec (+ (-> s :x) (-> s :w))))
             "Door is left of the store's right wall")
         (is (>= (-> s :door :y) (-> s :y)) 
             "Door is below the store's top wall")
         (is (<= (-> s :door :y) (dec (+ (-> s :y) (-> s :h))))
             "Door is above the store's bottom wall")
         (is (or (= (-> s :door :x) (-> s :x))
                 (= (-> s :door :x) (dec (+ (-> s :x) (-> s :w))))
                 (= (-> s :door :y) (-> s :y))
                 (= (-> s :door :y) (dec (+ (-> s :y) (-> s :h)))))
             "Door is on an exterior wall")))))


(deftest gen-town-stores-test
  (testing "Generaton of all stores in town"
     (dotimes [_ 100] ; Test with different randomization
       (let [s (gen-town-stores '(:a :b :c :d :e :f) 3)]
         (is (= (count s) 6))
         (is (some #(= (% :type) :a) s))
         (is (some #(= (% :type) :b) s))
         (is (some #(= (% :type) :c) s))
         (is (some #(= (% :type) :d) s))
         (is (some #(= (% :type) :e) s))
         (is (some #(= (% :type) :f) s))))))


(deftest gen-boundary-test
  (testing "Boundary generation"
     (let [b (gen-boundary 80 24)]
       (is (= (count b) 4)))))
       
