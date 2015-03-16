(ns clojack.plugins.animalme
  (:require [net.cgrand.enlive-html :as html]))

(defn run
  [_]
  (let [dom (html/html-resource
             (java.net.URL. "http://animalsbeingdicks.com/random"))
        img (html/select dom [:#content :.post :.entry :img])]
    (-> img
        first
        :attrs
        :src)))

(defn help
  []
  (str "*!animalme* "
       " Animals being Jerks. :cat2:"))
