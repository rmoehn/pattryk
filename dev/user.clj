; Requirements:
; - Read N tweets.
;   - N = Random number between 1 and 20.
;   - Which tweets? - From most followed users. Get here:
;     https://en.wikipedia.org/wiki/List_of_most_followed_users_on_Twitter Or
;     here: http://friendorfollow.com/twitter/most-followers/
;     - Select N randomly. Randomly select one of their last 10 tweets.
; - Post first word from first, second one from second, third one from third
;   etc. If there are not enough words, use the last.
;   - Punctuation following a word shall be included.
; - Should be possible to execute it in a loop.
; - Barebones error handling: everything throws an exception.

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
