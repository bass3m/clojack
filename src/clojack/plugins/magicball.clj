(ns clojack.plugins.magicball
  (:require [taoensso.timbre :as log]))

(def answers ["As I see it, yes."
              "It is certain."
              "It is decidedly so."
              "Most likely."
              "Outlook good."
              "Signs point to yes."
              "Without a doubt."
              "Yes."
              "Yes - definitely."
              "You may rely on it."
              "Reply hazy, try again."
              "Ask again later."
              "Better not tell you now."
              "Cannot predict now."
              "Concentrate and ask again."
              "Don't count on it."
              "My reply is no."
              "My sources say no."
              "Outlook not so good."
              "Very doubtful."])

(defn run
  []
  (log/info "8ball message")
  (rand-nth answers))

(def help-msgs ["Make your decisions easier !"
                "I have the answers to all your questions."
                "Just ask me a question."
                "I can answer any Yes/No question."
                "Concentrate on your question !"])

(defn help
  []
  (str "*!magicball* "
       (rand-nth help-msgs)
       " 100% guaranteed or your money back. :8ball:"))
