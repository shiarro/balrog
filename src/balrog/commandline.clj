(ns balrog.commandline
  (:use [clojure.string :only (lower-case upper-case)])) ;; Why no ' here?

;;
;; Functions to help parse the command line
;;
;; This code is intentionally non-compatible with moria, but I don't think
;; anyone will notice (or complain) (right?)
;;
;; To add oa new argument:
;;   1) Add the argument to valid-args, and possibly case-sensitive-args
;;   2) Update the help-text
;;   3) Define a parser function(s) for it
;;   4) Update is-valid-commandline to check for compatibility with other
;;      args if applicable
;;   5) Modify the calling code to call the parsing function(s) in step 2
;;

;; Define some constants related to command line processing
(def valid-args ;; Set of valid commandline arguments
  #{"-n" "-N" "-o" "-O" "-r" "-R" "-s" "-S" "-w" "-W"})

(def case-sensitive-args ;; Set of arguents are are case sensitive
  #{"-S" "-s"})

(def help-text ;; Print this when user needs help
  "Usage: moria [-norsw] [savefile]")


;; Application-specific predicate-like things to tell us what's in
;; the commandline
(defn new-game?
  "Return true if -n or -N is in the given command line"
  [args]
  (let [arg-set (set args)]
    (or (contains? arg-set "-N") (contains? arg-set "-n"))))


(defn use-original-keymap?
  "Return true if -o or -O is in the given command line"
  [args]
  (let [arg-set (set args)]
    (or (contains? arg-set "-O") (contains? arg-set "-o"))))


(defn use-rogue-like-keymap?
  "Return true if -r or -R is in the given command line"
  [args]
  (let [arg-set (set args)]
    (or (contains? arg-set "-R") (contains? arg-set "-r"))))


(defn show-scores-with-player?
  "Returns true if -S is given on the command line"
  [args]
  (let [arg-set (set args)] (contains? arg-set "-S")))


(defn show-scores-without-player?
  "Returns true if -s is given on the command line"
  [args]
  (let [arg-set (set args)] (contains? arg-set "-s")))


(defn wizard-mode?
  "Return true if -w or -W is in the given command line"
  [args]
  (let [arg-set (set args)]
    (or (contains? arg-set "-W") (contains? arg-set "-w"))))


(defn find-wizard-arg
  "Return a list where the first arg is the wizard arg, or nil if no
   wizard arg"
  [args]
  (loop [current-args args]
    (if (empty? current-args)
      nil
      (let [first-arg (first current-args)]
        (if (#{"-w" "-W"} first-arg)
          current-args
          (recur (rest current-args)))))))
      
            
(defn wizard-key
  "Return the random seed from command line if in wizard mode, -1 otherwise 
   or if not given"
  [args]
  (let [wizard-arg (find-wizard-arg args)]
    (if (or (< (count wizard-arg) 2)
            (not (integer? (read-string (second wizard-arg)))))
      -1
      (read-string (second wizard-arg)))))


(defn savefile
  "Return the name of the savefile from the commandline, if any.
   If none was given, return nil"
  [args]
  (if (or (< (count args) 1)
          (= (first (last args)) \-))
    nil
    (last args)))


;; Utility functions to aid in command line processing
(defn has-unrecognized-args?
  "Return true if the given commandline has any unrecognized arguments"
  [args]
  (loop [current-args args]
    (cond (empty? current-args) false
          (and (not (valid-args (first current-args)))
               (not (= (first current-args) (savefile current-args)))) true
        ;; Skip optional seed with -w
          :else (let [rest-of-args (if (and (#{"-w" "-W"} (first current-args)) 
                                            (not (= (wizard-key current-args) -1)))
                                     (rest (rest current-args))
                                     (rest current-args))]
                  (recur rest-of-args)))))


(defn has-duplicate-args?
  "Return true if an argument is given more than once.  Case-sensitivity
   as per original moria code is hard coded here."
  [args]
  (loop [current-args args
         found-args #{ }]
    (if (empty? current-args)
      false
      (let [first-arg (first current-args)]
        (if (found-args first-arg)
          true
          (let [lc-first-arg (lower-case first-arg)
                uc-first-arg (upper-case first-arg)
                new-arg-set (if (or (case-sensitive-args first-arg)
                                    (= lc-first-arg uc-first-arg))
                              #{ first-arg }
                              #{ lc-first-arg uc-first-arg })]
            (recur (rest current-args) (into found-args new-arg-set)))))))) 


(defn valid-commandline?
  "Returns true if the commandline is valid"
  [args]
  (and (not (has-unrecognized-args? args)) 
       (not (has-duplicate-args?))
       (not (and (use-rogue-like-keymap? args) (use-original-keymap? args)))
       (not (and (show-scores-with-player? args) 
                 (show-scores-without-player? args)))))

(defn get-help-text
  "Return the help text"
  []
  help-text)


