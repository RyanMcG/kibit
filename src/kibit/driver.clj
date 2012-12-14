(ns kibit.driver
  (:require [clojure.tools.namespace :refer [find-clojure-sources-in-dir]]
            [clojure.java.io :as io]
            [kibit.check :refer [check-file]]
            [kibit.reporters :refer [cli-reporter gfm-reporter]])
  (:use [clojure.tools.cli :only [cli]]))

(def reporters {"markdown" gfm-reporter
                "text" cli-reporter})

(def cli-specs [["-r" "--reporter"
                 "The reporter used when rendering suggestions"
                 :default "text"]])

(defn run [project & args]
  (let [[options file-args usage-text] (apply (partial cli args) cli-specs)
        source-files (if (empty? file-args)
                       (mapcat #(-> % io/file find-clojure-sources-in-dir)
                               (or (:source-paths project) [(:source-path project)]))
                       file-args)]
    (doseq [file source-files]
      (try (->> (check-file file)
                (map (reporters (:reporter options)))
                doall)
           (catch Exception e
             (println "Check failed -- skipping rest of file")
             (println (.getMessage e)))))))
