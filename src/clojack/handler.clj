(ns clojack.handler
  (:require [clojack.plugins :as plugins]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]
            [manifold.stream :as s]))

(defn load-plugins
  []
  (let [ps (into #{} (plugins/list-plugins))
        ignored #{"scan" "beer"}]
    (log/info "Loading the following plugins:" ps)
    (doseq [p ps]
      (require (symbol (str "clojack.plugins." p))))
    {:installed ps
     :ignored ignored}))

(defn plugin-help
  [p]
  (eval (read-string (str "(clojack.plugins." p "/help)"))))

(defn slack-response
  [txt msg]
  {:type "message"
   :text txt
   :channel (:channel msg)})

(defn bot-help [msg ws-stream plugins]
  (log/info "HELP message:" msg ":with plugins:" plugins)
  (let [help-msg (->> plugins
                      :installed
                      (map plugin-help)
                      (clojure.string/join "\n"))]
    @(s/put! ws-stream (json/write-str (slack-response help-msg msg)))))

(defn plugin-not-found
  [msg ws-stream]
  (let [flair [":confused:" ":sob:" ":cry:" ":scream:" ":poop:" ":question:"
               ":no_good:" ":scream_cat:" ":crying_cat_face:" ":goberserk:"
               ":hurtrealbad:" ":facepunch:"]
        txt (str (rand-nth flair) " No comprendo " (:text msg))]
    @(s/put! ws-stream (json/write-str (slack-response txt msg)))))

(defn plugin-run
  [plugin msg ws-stream]
  (let [output (eval
                (read-string
                 (str "(clojack.plugins." plugin "/run " (pr-str msg) ")")))]
    @(s/put! ws-stream (json/write-str (slack-response output msg)))))

(defn bot-cmd
  [cmds msg ws-stream plugins]
  (let [plugin (some (:installed plugins) cmds)]
    (cond
      plugin (plugin-run plugin msg ws-stream)
      (some (:ignored plugins) cmds) nil
      :else (plugin-not-found msg ws-stream))))

(defmulti handle-message
  (fn [msg ws-stream plugins] (:type msg)))

(defmethod handle-message "message"
  [msg ws-stream plugins]
  (log/info "Incoming Slack message:" msg ":plugins:" plugins)
  (when (:text msg)
    (condp re-find (:text msg)
      #"!help\s++|^!help$|<@\w+>: !help$" :>> (comp
                                                  (fn [msg] (bot-help msg ws-stream plugins))
                                                  (constantly msg))
      #"!(\w+)\s++|^!(\w+)$|<@\w+>: !(\w+)" :>> (fn [bot-cmd-matches]
                                                  (bot-cmd bot-cmd-matches
                                                           msg ws-stream
                                                           plugins))
      (str "Not found"))))

(defmethod handle-message :default
  [msg _ _]
  (log/info "Ignoring incoming Slack other message:" msg))
