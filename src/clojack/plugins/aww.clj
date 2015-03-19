(ns clojack.plugins.aww
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]))

(defn run
  [_]
  (let [dom (html/html-resource
             (java.net.URL. "http://www.reddit.com/r/aww.json"))
        awws (-> dom
                 first
                 :content
                 first
                 :content
                 first
                 (json/read-str :key-fn keyword)
                 :data
                 :children)
        aww (:data (nth awws (rand-int (count awws))))]
    (str "*" (:title aww) "*...\n" (:url aww))))

(defn help
  []
  (str "*!aww* "
       " Cute pictures. :cat:"))
