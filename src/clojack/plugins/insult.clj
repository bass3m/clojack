(ns clojack.plugins.insult
  (:require [taoensso.timbre :as log]))

(defn run
  []
  (log/info "Insult message fool")
  "An fresh insult")

(defn help
  []
  (log/info "Help message for insult plugin fool.")
  "*!insult* Get a free-range artisinal insult tailored just for you :trollface:")
