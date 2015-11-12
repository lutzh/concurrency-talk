(ns channels.core
  (:require [clojure.core.async :as async :refer [<! >! <!! >!! timeout chan alt! go go-loop]])
  (:require [org.httpkit.client :as http]))

(defn -main []

(def responses-channel (chan))

(go 
  (println "========================")
  (println (<! responses-channel))
  (println "========================"))

(go (http/get "http://api.urbandictionary.com/v0/define?term=freak" {}
          (fn [{:keys [status headers body error]}] (>! responses-channel body))))

(go (http/get "https://en.wiktionary.org/w/api.php?format=xml&action=query&rvprop=content&prop=revisions&redirects=1&titles=freak" {}
          (fn [{:keys [status headers body error]}] (>! responses-channel body))))

;; just so that it doesn't terminate before it puts out the text
(Thread/sleep 2000))
