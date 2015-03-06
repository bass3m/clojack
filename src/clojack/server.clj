(ns clojack.server
  (:require [clojure.core.async :as async :refer [<! <!! go-loop thread]]
            [clojack.handler :as h]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [aleph.http :as http :only [get websocket-client]]
            [manifold.stream :as s]
            [byte-streams :as bs])
  (:gen-class))

(defn enable-logging
  []
  (log/set-config! [:appenders :spit :enabled?] true)
  (log/set-config! [:shared-appender-config :spit-filename]
                   (or (env :logfile) "clojack.log")))

(defn pinger
  [ws-stream]
  (let [ping {:id 0
              :type "ping"}]
    (async/go-loop [id 0]
      (<! (async/timeout 10000))
      @(s/put! ws-stream
               (json/write-str (assoc ping :id id)))
      (recur (inc id)))))

(defn event-loop
  [ws-stream]
  (pinger ws-stream)
  (loop []
    (when-let [msg @(s/take! ws-stream)]
      (-> msg (json/read-str :key-fn keyword) (h/handle-message ws-stream))
      (recur))))

(defn rtm-start
  "make a synchronous connection to slack and grab the ws url"
  []
  (let [options {:query-params {:token (env :slack-token)}}
        {:keys [status headers body error]} @(http/get
                                              "https://slack.com/api/rtm.start"
                                              options)]
    (if error
      (do
        (log/error "Failed connecting to slack. Error:" error)
        nil)
      (as-> body _
        (bs/to-string _)
        (json/read-str _)
        (zipmap (map keyword (keys _)) (vals _))))))

(defn rtm-connect
  [ws-url]
  (let [ws-stream @(http/websocket-client ws-url)]
    ws-stream))

(defn run []
  (-> (rtm-start) :url rtm-connect event-loop))

(defn -main
  []
  (enable-logging)
  (run))
