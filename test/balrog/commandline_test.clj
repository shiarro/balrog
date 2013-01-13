(ns balrog.commandline-test
  (:use clojure.test
        balrog.commandline))

;; All valid options should be accepted
(deftest new-game-test
  (testing "New game option"
    (is (= (new-game? '("-n" "-x" "-y")) true))
    (is (= (new-game? '("-x" "-n" "-y")) true))
    (is (= (new-game? '("-x" "-y" "-n")) true))
    (is (= (new-game? '("-N")) true))
    (is (= (new-game? '("-x")) false))))

(deftest orig-keymap-test
  (testing "Original keymap option"
    (is (= (use-original-keymap? '("-o" "-x" "-y")) true))
    (is (= (use-original-keymap? '("-x" "-o" "-y")) true))
    (is (= (use-original-keymap? '("-x" "-y" "-o")) true))
    (is (= (use-original-keymap? '("-O")) true))
    (is (= (use-original-keymap? '("-n")) false))))

(deftest rogue-like-keymap-test
  (testing "Rogue-like keymap option"
    (is (= (use-rogue-like-keymap? '("-r" "-x" "-y")) true))
    (is (= (use-rogue-like-keymap? '("-x" "-r" "-y")) true))
    (is (= (use-rogue-like-keymap? '("-r" "-x" "-y")) true))
    (is (= (use-rogue-like-keymap? '("-R")) true))
    (is (= (use-rogue-like-keymap? '("-z")) false))))

(deftest show-scores-with-player-test
  (testing "Show scores with player option"
    (is (= (show-scores-with-player? '("-S" "-a" "-b"))) true)
    (is (= (show-scores-with-player? '("-a" "-S" "-c"))) true)
    (is (= (show-scores-with-player? '("-S" "-b" "-c"))) true)
    (is (= (show-scores-with-player? '("-s"))) false)
    (is (= (show-scores-with-player? '("-q"))) true)))

(deftest show-scores-without-player-test
  (testing "Show scores without player option"
    (is (= (show-scores-without-player? '("-s" "-a" "-b"))) true)
    (is (= (show-scores-without-player? '("-a" "-s" "-c"))) true)
    (is (= (show-scores-without-player? '("-s" "-b" "-c"))) true)
    (is (= (show-scores-without-player? '("-S"))) false)
    (is (= (show-scores-without-player? '("-q"))) true)))


(deftest wizard-mode-test
  (testing "Wizard mode option"
    (is (= (wizard-mode? '("-w" "-a" "-b")) true))
    (is (= (wizard-mode? '("-W" "-a" "-b")) true))
    (is (= (wizard-mode? '("-w" "100" "-a" "-b")) true))
    (is (= (wizard-mode? '("-W" "100" "-a" "-b")) true))
    (is (= (wizard-mode? '("-a" "-w" "-b")) true))
    (is (= (wizard-mode? '("-a" "-W" "-b")) true))
    (is (= (wizard-mode? '("-a" "-w" "100" "-b")) true))
    (is (= (wizard-mode? '("-a" "-W" "100" "-b")) true))
    (is (= (wizard-mode? '("-a" "-b" "-w")) true))
    (is (= (wizard-mode? '("-a" "-b" "-W")) true))
    (is (= (wizard-mode? '("-a" "-b" "-w" "100")) true))
    (is (= (wizard-mode? '("-a" "-b" "-W" "100")) true))
    ))

(deftest wizard-key-test
  (testing "Wizard key option"
    (is (= (wizard-key '("-w")) -1))
    (is (= (wizard-key '("-W")) -1))
    (is (= (wizard-key '("-w" "100")) 100))
    (is (= (wizard-key '("-W" "100")) 100))
    (is (= (wizard-key '("-w" "-a" "-b")) -1))
    (is (= (wizard-key '("-W" "-a" "-b")) -1))
    (is (= (wizard-key '("-w" "100" "-a" "-b")) 100))
    (is (= (wizard-key '("-W" "100" "-a" "-b")) 100))
    (is (= (wizard-key '("-a" "-w" "-b")) -1))
    (is (= (wizard-key '("-a" "-W" "-b")) -1))
    (is (= (wizard-key '("-a" "-w" "100" "-b")) 100))
    (is (= (wizard-key '("-a" "-W" "100" "-b")) 100))
    (is (= (wizard-key '("-a" "-b" "-w")) -1))
    (is (= (wizard-key '("-a" "-b" "-W")) -1))
    (is (= (wizard-key '("-a" "-b" "-w" "100")) 100))
    (is (= (wizard-key '("-a" "-b" "-W" "100")) 100))
    (is (= (wizard-key '("-a" "-b")) -1))))

(deftest savefile-test
  (testing "Save file argument"
     (is (= (savefile '("-x" "-y" "-z" "file.save")) "file.save"))
     (is (nil? (savefile '("-x" "-y" "-z"))))
     (is (nil? (savefile '("file.save" "-x"))))))

     
;; Duplicate options should flag an error
(deftest duplicate-arg-test
  (testing "Duplicate command line arguments"
     (is (= (has-duplicate-args? '("-n" "-n")) true))
     (is (= (has-duplicate-args? '("-n" "-N")) true))
     (is (= (has-duplicate-args? '("-N" "-n")) true))
     (is (= (has-duplicate-args? '("-N" "-N")) true))
     (is (= (has-duplicate-args? '("-o" "-o")) true))
     (is (= (has-duplicate-args? '("-r" "-r")) true))
     (is (= (has-duplicate-args? '("-w" "-w")) true))
     (is (= (has-duplicate-args? '("-w" "100" "-w")) true))
     (is (= (has-duplicate-args? '("-n" "-r")) false))
     (is (= (has-duplicate-args? '("-o" "-N")) false))
     (is (= (has-duplicate-args? '("-w" "100" "-n")) false))
     (is (= (has-duplicate-args? '("-W" "20" "-R")) false))))


;; Unrecognized options should flag an error
(deftest unrecognize-arg-test
  (testing "Unrecognized arguments"
     (is (= (has-unrecognized-args? '("-n")), false))
     (is (= (has-unrecognized-args? '("-N")), false))
     (is (= (has-unrecognized-args? '("-o")), false))
     (is (= (has-unrecognized-args? '("-O")), false))
     (is (= (has-unrecognized-args? '("-r")), false))
     (is (= (has-unrecognized-args? '("-R")), false))
     (is (= (has-unrecognized-args? '("-s")), false))
     (is (= (has-unrecognized-args? '("-S")), false))
     (is (= (has-unrecognized-args? '("-w")), false))
     (is (= (has-unrecognized-args? '("-W")), false))
     (is (= (has-unrecognized-args? '("-w" "999")), false))
     (is (= (has-unrecognized-args? '("-W" "999")), false))
     (is (= (has-unrecognized-args? '("-W" "999" "1000" "1001")), true))
     (is (= (has-unrecognized-args? '("-X")), true))
     (is (= (has-unrecognized-args? '("-y")), true))
     (is (= (has-unrecognized-args? '("-x")), true))
     (is (= (has-unrecognized-args? '("file.save" "-s")), true))
     (is (= (has-unrecognized-args? '("file.save")), false))
     (is (= (has-unrecognized-args? 
              '("-n" "-O" "-r" "-s" "-W" "999" "file.save")), false))
     (is (= (has-unrecognized-args? 
              '("-n" "-O" "-t" "-s" "-W" "999" "file.save")), true))))


;; Illegal option combinations should flag an error
