(ns balrog.core (:gen-class))

;; Keep the game state in a big-ass map.  There are more advances facilities
;; in clojure for this, but I don't want to take the time ti figure them
;; out right now
(def game-state 
  { :rand_seed 0 })

(defn -main
  "Parse the command line and run the game"
  [& main-args]
  (println "Welcome to balrog")
  
  ;; Not sure where the command line will show up, so check a few places  
  (loop [args (if (nil? *command-line-args*) 
                main-args
                *command-line-args*)]
    (if (empty? args)
      nil
      (let [first-arg (first args)]
        (when (string? first-arg) (println first-arg ": string"))
        (when (integer? first-arg) (println first-arg ": integer"))
        (recur (rest args))))))
