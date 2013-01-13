(ns balrog.mapquery
  "Functions to look stuff up in the world map")


(defn mapobj-contains?
  "Determine if the given map object contains the given point.
   mapobj must be a map (clojure type) with keys :x :y :w :h, or
  just :x :y for a single-point object"
  [mapobj x y]
  (cond 
    (and (contains? mapobj :x) (contains? mapobj :y)
         (contains? mapobj :w) (contains? mapobj :h))
    (and (>= x (-> mapobj :x))
         (< x (+ (-> mapobj :x) (-> mapobj :w)))
         (>= y (-> mapobj :y))
         (< y (+ (-> mapobj :y) (-> mapobj :h))))

    (and (contains? mapobj :x) (contains? mapobj :y))
    (and (= x (-> mapobj :x)) (= y (-> mapobj :y)))
          
    ; TODO: come up with an error handling strategy and update this
    :else (throw (IllegalArgumentException. 
                   (str "Unsupported mapobj type " (mapobj :objtype))))))
  
  
(defn any-mapobj-contains?
  "Run mapobj-contains? on everything in the given collection"
  ; TODO: Make this a macro
  [mapobjs x y]
  (some #(mapobj-contains? % x y) mapobjs))


(defn filter-mapobj-contains
  "Return a list of objects that are at the given point"
  [mapobjs x y]
  ; TODO: make this a macro
  (filter #(mapobj-contains? % x y) mapobjs))


(defn open-space?
  "Return true if the given location is an open space i.e. one we can
   put something in"
  [mapobjs x y]
  ; TODO: Make this work for hollow objects
  ; TODO: Make this a macro
  (not (any-mapobj-contains? mapobjs x y)))


(defn wall?
  "Return true if the given location is a wall"
  [mapobjs x y]
  (some #(or (= (% :objtype) :boundary) 
             (= (% :objtype) :store)) ; Consider the door a wall too 
           (filter-mapobj-contains mapobjs x y)))

  
(defn count-adj-walls
  "Count the number of walls adjacent to the given location not including
   diagonals
   umoria source: misc1.c/next_to_walls()"
  [mapobjs x y]
  (apply + (map #(if (wall? (% 0) (% 1)) 1 0)
                '([(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]))))
  

(defn find-first-open-space
  "Find the first open space in the given search area, starting from the upper
   left and working right and then down.  Return it in a map with :x and :y keys.
   umoria source: generate.c/place_stairs()"
  [mapobjs x y w h min-adj-walls]
  (loop [cur-x x cur-y y]
    (cond 
      (and (open-space? mapobjs cur-x cur-y) 
           (>= (count-adj-walls mapobjs cur-x cur-y) min-adj-walls))
      {:x cur-x :y cur-y}
      
      (and (>= (inc cur-x) (+ x w)) (>= (inc cur-y) (+ y h))) 
      nil
      
      (>= (inc cur-x) (+ x w)) 
      (recur x (inc cur-y))
      
      :else 
      (recur (inc cur-x) cur-y))))

