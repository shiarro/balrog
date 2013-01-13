(ns balrog.mapgen
  "Functions to generate maps - walls, lighting, doors"
  (:use [balrog.utilities :only (randint remove-nth)]))


(def default-map-config
  "Default map configuration. 
   TODO: Get this from a file"
  {:town {:stores {:types [:general-store 
                            :armory 
                            :weaponsmith 
                            :temple 
                            :alchemy-shop 
                            :magic-users-store]
                   :grid  {:x 3 :y 2}}}
   ; TODO: Vary town size based on window size(?)
   :w 80
   :h 24})


(defn get-default-map-config
  "Like it says. This is a good place to start if you want to modify
   something while keeping all the required fields intact"
  []
  default-map-config)


(defn gen-store
  "Generate a store at in the given town grid location.  
   Returns a map like this
   { :objtype :store
     :type <type>            ; Whatever you pass in
     :x <x> :y <y>           ; Origin (upper-left corner)
     :w <width> :h <height>  ; Size of store
     :door {:x <x> :y <y>}}  ; Absolute coordinates
   umoria source: generate.c/build-store()"   
  [store-type grid-x grid-y]
  ; "Pre-fetch" all the random numbers we can here to keep randint call order 
  ; the same as umoria. We can't prefech the last one (used for door location)
  ; because we don't know what the range is yet.
  (let [r        [(randint 3) (randint 4) (randint 6) (randint 6) (randint 4)]
        y-val    (+ (* grid-y 10) 5)
        x-val    (+ (* grid-x 16) 16)
        origin-y (- y-val (r 0))
        origin-x (- x-val (r 2))
        width    (inc (- (+ x-val (r 3)) origin-x))
        height   (inc (- (+ y-val (r 1)) origin-y))]
    {:objtype :store
     :x       origin-x 
     :y       origin-y
     :w       width    
     :h       height
     :type    store-type
     :door    (condp = (r 4)
                1 {:x origin-x 
                   :y (dec (+ (randint (dec height)) origin-y))}
                2 {:x (dec (+ origin-x width)) 
                   :y (dec (+ (randint (dec height)) origin-y))}
                3 {:x (dec (+ (randint (dec width)) origin-x))
                   :y origin-y}
                4 {:x (dec (+ (randint (dec width)) origin-x))
                   :y (dec (+ origin-y height))})
     }))


(defn gen-town-stores
  "Generate the stores for the town level
   umoria source: generate.c/town_gen(void) (near top)"
  [types max-x]
  ; TODO: Set seed town-seed somehow
  (loop [stores nil
         stores-left types
         cur-x 0
         cur-y 0]
    (if (= (count stores-left) 0)
      stores
      (let [r (dec (randint (count stores-left)))
            goto-next-y? (= (inc cur-x) max-x)]
        (recur (conj stores (gen-store (nth stores-left r) cur-x cur-y))
               (remove-nth stores-left r)
               (if goto-next-y? 0 (inc cur-x))
               (if goto-next-y? (inc cur-y) cur-y))))))


(defn gen-boundary
  "Generate boundary walls around the edge of the current level
   umoria source: generate.c/place_boundary()"
  [width height]
  '({:objtype :boundary :x 0           :y 0            :w width :h 1}
    {:objtype :boundary :x 0           :y 0            :w 1     :h height}
    {:objtype :boundary :x (dec width) :y 0            :w 1     :h height}
    {:objtype :boundary :x 0           :y (dec height) :w width :h 1}))  

    

(defn gen-town
  "Generate the town level with the given stores in it
   umoria source: generate.c/town_gen(void)"
  [town-config]
  ; TODO: Set seed town-seed somehow
  {:objects (into (gen-town-stores (-> town-config :stores :types)
                                   (-> town-config :stores :grid :x))
                  (gen-boundary (-> town-config :w)
                                (-> town-config :h)))
   :w (-> town-config :w) 
   :h (-> town-config :h)})


(defn gen-cave
  "Generate a level in the dungeon
  umoria source: generate.c/..."
  []
  '())


(defn generate-level
  "Generate a map of the current level and return it
   umoria source: generate.c/generate_cave()"
  [map-config level]
  (if (= level 0)
    (gen-town (-> map-config :town))
    (gen-cave)))
