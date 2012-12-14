(ns kibit.test.reporters
  "Test various reporters that kibit makes avaialble."
  (:require [kibit.reporters :as reporters]
            [clojure.string :as string])
  (:use [clojure.test]))


(def test-map {:file "imaginary_file.clj" :line 100 :expr '#(:name %) :alt ':name})

(deftest report-output
  (are [reporter output] (= (with-out-str (reporter test-map)) output)
       reporters/cli-reporter (string/join
                                "\n"
                                ["At imaginary_file.clj:100:\n"
                                 "Consider using:"
                                 "  :name"
                                 "instead of:"
                                 "  #(:name %)\n"])
       reporters/gfm-reporter ["At `imaginary_file.clj:100`:"
                               "Consider using:"
                               "```clojure"
                               "  :name"
                               "```"
                               "instead of:"
                               "```clojure"
                               "  #(:name %)"
                               "```\n"]))
