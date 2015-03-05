(ns insultomatic.server
  (:require
   ;; [insultomatic.handler :as h]
   [taoensso.timbre :as log]
   [clojure.data.json :as json]
   [environ.core :refer [env]]
   [aleph.http :as http :only [get websocket-client]]
   [manifold.stream :as s]
   [byte-streams :as bs])
  (:gen-class))

(def url "https://slack.com/api/rtm.start")

(defn enable-logging
  []
  (log/set-config! [:appenders :standard-out :rate-limit] [1 10000])
  (log/set-config! [:appenders :spit :enabled?] true)
  (log/set-config! [:shared-appender-config :spit-filename]
                   (or (env :logfile) "insultomatic.log")))

(defn incoming-msg
  [slack-msg]
  (log/info "Incoming msg:" slack-msg))

(defn outgoing-msg [])

(defn event-loop
  [ws-conn]
  (loop [msg @(s/take! ws-conn)]
    (incoming-msg msg)
    (recur @(s/take! ws-conn))))

(defn rtm-start
  "make a synchronous connection to slack and grab the ws url"
  []
  (let [options {:query-params {:token (or (env :slack-token) token)}}
        {:keys [status headers body error]} @(http/get
                                              "https://slack.com/api/rtm.start"
                                              options)]
    (if error
      (do
        (println "Failed connecting to slack, exception is " error)
        nil)
      (as-> body _
        (bs/to-string _)
        (json/read-str _)
        (zipmap (map keyword (keys _)) (vals _))))))

(defn rtm-connect
  [ws-url]
  (let [ws-conn @(http/websocket-client ws-url)]
    (log/info "Got WS-conn:" ws-conn)
    (s/consume incoming-msg ws-conn)
    ;; (s/connect ws-conn ws-conn)
    ws-conn))

(defn run []
  (enable-logging)
  (if-let [slack-conn (rtm-start)]
    (-> slack-conn :url rtm-connect event-loop)
    (log/error "Failed to connect with Slack.")))

(defn -main
  []
  (run))
