# clojack
Yet another __Clojure Slack__ bot.

##### What is it ?
Clojack is a bot that listens and reacts to messages from Slack. It uses a simple plugin-based system to support additional commands.

##### How ?
Clojack uses the Slack Real Time Messaging API [Slack Real Time Messaging API](https://api.slack.com/rtm). It is a WebSocket based API and allows Clojack to recieve Slack events in real time, and also provides means for sending messages to Slack.

##### How to add custom plugins ?
In order to add a new plugin, you only need to implement two functions :

* help : invoked with no arguments, returns a help string describing how to use the plugin. 
* run : takes an argument (which is the message from slack) and returns a string with the result of running the plugin. This will be the string that will be posted to Slack. 
