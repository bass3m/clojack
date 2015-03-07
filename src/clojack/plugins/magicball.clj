(ns clojack.plugins.magicball
  (:require [taoensso.timbre :as log]))

(defn run
  []
  (log/info "8ball message")
  "An 8Ball Message")

(defn help
  []
  "*!magicball* Ask me a question and I will give you the outcome with certainty. 100% guaranteed or your money back ! :8ball:")
