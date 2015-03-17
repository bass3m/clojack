(ns clojack.plugins.excuse
  (:require [net.cgrand.enlive-html :as html]))

(defn run
  [_]
  (let [dom (html/html-resource
             (java.net.URL. "http://developerexcuses.com/"))
        excuse (html/select dom [:.wrapper :a])]
    (-> excuse
        first
        :content
        first)))

(defn help
  []
  (str "*!excuse* "
       " What's my excuse ? :construction:"))
