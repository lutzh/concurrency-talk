(ns channels.core
  (:require [clojure.core.async :as async :refer [<! >! <!! >!! timeout chan alt! go go-loop]])
  (:require [org.httpkit.client :as http]))

(defn -main []

(def responses-channel (chan 10))

(go 
  (println "========================")
  (println (take 2 responses-channel))
  (println "========================"))

(defn async-get [url result]
  (http/get url #(go (>! result (:body %)))))

(async-get "https://en.wiktionary.org/w/api.php?format=xml&action=query&rvprop=content&prop=revisions&redirects=1&titles=freak" responses-channel)
(async-get "http://api.urbandictionary.com/v0/define?term=freak" responses-channel)

;; just so that it doesn't terminate before it puts out the text
(Thread/sleep 2000))
