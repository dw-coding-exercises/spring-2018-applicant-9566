; Thanks for the opportunity to apply my knowledge of programming and routes to a Clojure!

(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;[clj-http.client :as client] ; I added the clj-http.client here in lieu of directly calling curl. It's commented due to technical issues.
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [my-exercise.home :as home]))

; This function simplifies the code for adding values
(defn ocdid [id val]
  "Produce the ocd id for a particular val"
  (if (clojure.string/blank? val) ; If the user leaves the area blank, do not include it in the search
  (str nil)
  (concat (concat (concat "/" id) ":") (clojure.string/lower-case (clojure.string/replace val #" " "_")))))
; Notice that the id does not have to be lowercase here, since this is not based on user input.


(defroutes app
  (GET "/" [] home/page)
  ; The order of the routes matter. The POST occurs before the "catch all" route.
  (POST "/search" req
      (let [street (get (:params req) :street)
            street-2 (get (:params req) :street-2)
            city (get (:params req) :city)
            state (get (:params req) :state)
            zip (get (:params req) :zip)]

        ; Define the state and place.
        (def state_ocdid (concat (concat "ocd-division/country:us" (ocdid "state" state)))) ; Ooooo ocdid Fxn Call!
        (def place_ocdid (concat state_ocdid (ocdid "place" city))) ; Ooooo another ocdid Fxn call! :-)

        ; I know you didn't ask for this, but I was having fun! I've never dealt with Clojure before!
        (def full_ocdid (concat (concat (concat (concat (concat (concat "ocd-division/country:us" (ocdid "street" street)) (ocdid "street-2" street-2))) (ocdid "place" city)) (ocdid "state" state)) (ocdid "zip" zip)))

        ;If given the chance to do a next version, I would refactor to clean up the multiple parentheses with the concat function.
        (def api (concat (concat (concat (concat "'https://api.turbovote.org/elections/upcoming?district-divisions=" state_ocdid) ",") place_ocdid) "'"))

        ; I did not get to test this due to technical issues. This line is commented just to make sure it at least compiles.
        ; (client/get api)

        ;TESTING, although rudimentary!
        ; (println state_ocdid)
        ; (println place_ocdid)
        ; (println full_ocdid)
        ; (println api) ; I have to get used to putting the parentheses outside! :-)

        ))

  (route/resources "/")
  (route/not-found "Not found")
)



(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))
