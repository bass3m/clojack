(ns clojack.handler
  (:require [clojack.plugins :as plugins]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]
            [manifold.stream :as s]))

(defn load-plugins
  []
  (let [ps (into #{} (plugins/list-plugins))]
    (log/info "Loading the following plugins:" ps)
    (doseq [p ps]
      (require (symbol (str "clojack.plugins." p))))
    ps))

(defn plugin-help
  [p]
  (eval (read-string (str "(clojack.plugins." p "/help)"))))

(defn bot-help [msg ws-stream plugins]
  (log/info "HELP message:" msg ":with plugins:" plugins)
  (let [help-msg (->> plugins
                      (map plugin-help)
                      (clojure.string/join "\n"))
        rsp {:type "message"
             :text help-msg
             :channel (:channel msg)}]
    @(s/put! ws-stream (json/write-str rsp))))

(defn plugin-not-found
  [msg ws-stream]
  (let [flair [":confused:" ":sob:" ":cry:" ":scream:" ":poop:" ":question:"
               ":no_good:" ":scream_cat:" ":crying_cat_face:" ":goberserk:"
               ":hurtrealbad:" ":facepunch:"]
        rsp {:type "message"
             :text (str (rand-nth flair) " No comprendo " (:text msg))
             :channel (:channel msg)}]
    @(s/put! ws-stream (json/write-str rsp))))

(defn plugin-run
  [plugin msg ws-stream]
  (let [output (eval (read-string (str "(clojack.plugins." plugin "/run)")))
        rsp {:type "message"
             :text output
             :channel (:channel msg)}]
    @(s/put! ws-stream (json/write-str rsp))))

(defn bot-cmd
  [cmds msg ws-stream plugins]
  (if-let [plugin (some plugins cmds)]
    (plugin-run plugin msg ws-stream)
    (plugin-not-found msg ws-stream)))

(defn insult [msg ws-stream]
  (log/info "Insult message fool:" msg))

(defmulti handle-message
  (fn [msg ws-stream plugins] (:type msg)))

(defmethod handle-message "message"
  [msg ws-stream plugins]
  (log/info "Incoming Slack message:" msg ":plugins:" plugins)
  (condp re-find (:text msg)
    #"^!help\s++|^!help$" :>> (comp
                               (fn [msg] (bot-help msg ws-stream plugins))
                               (constantly msg))
    #"^!(\w+)\s++|^!(\w+)$" :>> (fn [bot-cmd-matches]
                                  (bot-cmd bot-cmd-matches msg ws-stream plugins))
    (str "Not found")))

(defmethod handle-message :default
  [msg _ _]
  (log/info "Ignoring incoming Slack other message:" msg))
