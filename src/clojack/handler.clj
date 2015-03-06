(ns clojack.handler
  (:require [taoensso.timbre :as log]
            [clojure.data.json :as json]
            [manifold.stream :as s]))

(def help-msg "*!help* This message.\n*!insult* Get a free range artisinal insult just for you\n")

(defn help-bot [msg ws-stream]
  (log/info "HELP message:" msg)
  (let [rsp {:type "message"
             :text help-msg
             :channel (:channel msg)}]
    @(s/put! ws-stream (json/write-str rsp))))

(defn insult [msg ws-stream]
  (log/info "Insult message fool:" msg))

(defmulti handle-message
  (fn [msg ws-stream] (:type msg)))

(defmethod handle-message "message"
  [msg ws-stream]
  (log/info "Incoming Slack message:" msg)
  (condp re-find (:text msg)
    #"^!help\s++|^!help$" :>> (comp
                               (fn [msg] (help-bot msg ws-stream))
                               (constantly msg))
    #"^!insult\s++|^!insult$" :>> (comp
                                   (fn [msg] (insult msg ws-stream))
                                   (constantly msg))
    (str "Not found")))

(defmethod handle-message :default
  [msg _]
  (log/info "Ignoring incoming Slack other message:" msg))
