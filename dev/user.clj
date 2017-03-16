(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [pst doc find-doc]]
            [clojure.string :as string]
            [clojure.tools.namespace.repl :refer [refresh]]
            [net.cgrand.enlive-html :as enlive]
            [twitter.oauth]
            [twitter.api.restful :as twitter]))

(def most-followers-url-s
  "http://friendorfollow.com/twitter/most-followers/")

(def twitter-creds
  (twitter.oauth/make-oauth-creds
    (System/getenv "CLOVEZ_TWITTER_CONSUMER_KEY")
    (System/getenv "CLOVEZ_TWITTER_CONSUMER_SECRET")
    (System/getenv "CLOVEZ_TWITTER_ACCESS_TOKEN")
    (System/getenv "CLOVEZ_TWITTER_ACCESS_TOKEN_SECRET")))

(defn fetch-page [url-s]
  (enlive/html-resource (io/as-url url-s)))

(defn get-screen-names [resource]
  (->> (enlive/select resource [:a.tUser])
       (map enlive/text)
       (map #(string/replace-first % "@" ""))))

(defn get-recent-tweets [twitter-creds screen-name]
  (->> (twitter/statuses-user-timeline
         :oauth-creds twitter-creds
         :params {:screen-name screen-name
                  :count 20
                  :include_rts false
                  :trim_user true})
       :body
       (map :text)))

(defn get-random-tweet [twitter-creds screen-name]
  (rand-nth (get-recent-tweets twitter-creds screen-name)))

(defn get-random-tweets [twitter-creds screen-names]
  (->> screen-names
       (random-sample 0.05)
       shuffle
       (map #(get-random-tweet twitter-creds %))))

(defn make-silly-text [tweets]
  (->> tweets
       (map #(string/split % #" "))
       (map-indexed #(get %2 %1 (count %2)))
       (string/join " ")))

(defn tweet-silly-text [twitter-creds screen-names]
  (let [silly-text (make-silly-text
                     (get-random-tweets twitter-creds
                                        screen-names))]
    (twitter/statuses-update :oauth-creds twitter-creds
                             :params {:status silly-text})))

(comment

  (def mf-res (fetch-page most-followers-url-s))

  (def screen-names (get-screen-names mf-res))

  (loop []
    (println "Assembling next silly tweet…")
    (tweet-silly-text twitter-creds screen-names)
    (Thread/sleep 10000)
    (recur))


  )

; Requirements:
; - Read N tweets.
;   - N = Random number between 1 and 20.
;   - Which tweets? - From most followed users. Get here:
;     https://en.wikipedia.org/wiki/List_of_most_followed_users_on_Twitter Or
;     here: http://friendorfollow.com/twitter/most-followers/
;     - Select N randomly. Randomly select one of their last 10 tweets.
;       - Twitter returns retweets by default, and if you exclude them, they're
;         still counted. Therefore, set this count to 20.
; - Post first word from first, second one from second, third one from third
;   etc. If there are not enough words, use the last.
;   - Punctuation following a word shall be included.
; - Should be possible to execute it in a loop.
; - Barebones error handling: everything throws an exception.
; Non-functional:
; - Make it as primitive as possible.

; Design:
; - Read list of most followed statically.
; - Single entry point: (tweet-random account-name)s
; - Components:
;   - Most followed user obtaining and parsing and returning.
;     - Download directly using Enlive. Error handling not so important in this
;       case.
;   - Selecting of users to use tweets from.
;   - (User search.) – Should already get the proper names from the list.
;   - Read recent tweets from user.
;     - Using the Twitter library. Something asynchronous and go-routines if
;       it's too slow.
;     - Randomly select tweet from recent tweets.
;   - Chop up tweets into words. – Just with string/split or what they have.
;   - Select progressing index words from chopped up tweets.
;   - Assemble.
;   - Tweet to my account. Again with Twitter library

; - How does Twitter access work? What do I need to pass around?
;   - A Twitter creds object.
;   - Def it globally? Or pass it in? Pass it in. Because that's what I would do
;     if I was writing a component.
;(defn make-silly-text [tweets]
;  (->> tweets
;       (map #(string/split % #" "))
;       (map-indexed #(get %2 %1 (last %2)))
;       (string/join " "))

