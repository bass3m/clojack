(defproject insultomatic "0.1.0-SNAPSHOT"
  :description "Slack Bot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [aleph "0.4.0-beta3"]
                 [com.taoensso/timbre "3.4.0"]
                 ;;[org.msync/properties-clj "0.3.1"]
                 [environ "1.0.0"]
                 [org.clojure/data.json "0.2.5"]
                 ;;[leiningen "2.5.1"]
                 [clj-time "0.9.0"]
                 [com.cemerick/piggieback "0.1.5"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.1"]]
  :min-lein-version "2.5.0"
  :uberjar-name "insultomatic.jar"
  :main insultomatic.server
)
