(defproject clojack "0.1.0-SNAPSHOT"
  :description "Slack Bot"
  :url "https://github.com/bass3m/clojack"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [aleph "0.4.0-beta3"]
                 [com.taoensso/timbre "3.4.0"]
                 [environ "1.0.0"]
                 [org.clojure/data.json "0.2.5"]
                 [clj-time "0.9.0"]
                 [com.cemerick/piggieback "0.1.5"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.1"]]
  :min-lein-version "2.5.0"
  :uberjar-name "clojack.jar"
  :main clojack.server
)
