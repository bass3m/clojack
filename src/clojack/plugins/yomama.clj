(ns clojack.plugins.yomama
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]))

(defn run
  [_]
  (try
    (let [dom (html/html-resource
               (java.net.URL. "http://www.reddit.com/r/mommajokes.json"))
          yomamas (-> dom
                      first
                      :content
                      first
                      :content
                      first
                      (json/read-str :key-fn keyword)
                      :data
                      :children)
          joke (:data (nth yomamas (rand-int (count yomamas))))]
      (str "*" (:title joke) "*...\n" (:selftext joke)))
    (catch Exception e
      (let [flair [":confused:" ":sob:" ":cry:" ":scream:" ":poop:" ":question:"
                   ":no_good:" ":scream_cat:" ":crying_cat_face:" ":goberserk:"
                   ":hurtrealbad:" ":facepunch:"]]
        (str (rand-nth flair) " No comprendo!")))))

(defn help
  []
  (str "*!yomama* "
       " Jokes about Yo Mama. :older_woman:"))
