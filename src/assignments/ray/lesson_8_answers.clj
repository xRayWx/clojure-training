(ns lesson-8-answers
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan alts!!]]))

(def bing "https://www.bing.com/search?q=")
(def yahoo "https://au.search.yahoo.com/search?p=")
(def yippy "https://www.yippy.com/search?query=")

(defn search [search-engine search-term]
  (slurp (str search-engine search-term)))

;; Search with futures & promises
(defn fp-search [search-term]
  (let [result (promise)]
    (doseq [search-engine [bing yahoo yippy]]
      (future (deliver result (search search-engine search-term))))
    @result))

(time (fp-search "clojure"))

;; Search with core.async
(defn async-search [search-term]
  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (go (>! c1 (search bing search-term)))
    (go (>! c2 (search yahoo search-term)))
    (go (>! c3 (search yippy search-term)))
    (let [[result c] (alts!! [c1 c2 c3])]
      result)))

(time (async-search "clojure"))

