(ns clojack.plugins)

(defn only-files
  [file-s]
  (filter (fn [file] (.isFile file)) file-s))

(defn file-names
  [file-s]
  (map (fn [file] (.getName file)) file-s))

(defn keep-clj
  [file-s]
  (map (fn [file] (second (re-find #"^(\w+)\.clj$" file))) file-s))

(defn list-plugins
  []
  (->> "./src/clojack/plugins"
       clojure.java.io/file
       file-seq
       only-files
       file-names
       keep-clj
       (remove nil?)))
