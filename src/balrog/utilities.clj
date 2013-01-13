(ns balrog.utilities
  "Utility functions used by the rest of the code")


(defmacro randint
  "Generate a random integer between 1 and n (inclusive both ends).
   umoria source: misc1.c/randint(void)"
  [n]
  `(inc (rand-int ~n)))


(defn remove-nth
  "Remove the nth item from the given collection"
  [c n]
  (cond
    (or (= (count c) 0) (< n 0) (> n (dec (count c)))) c
    (= n 0) (rest c)
    (= n (dec (count c))) (butlast c)
    :else (into (drop (inc n) c) (take n c)))) ; Keep the correct order

      