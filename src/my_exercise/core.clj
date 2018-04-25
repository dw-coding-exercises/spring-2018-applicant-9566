; Thanks for the opportunity to apply my knowledge of programming and routes to a Clojure!

(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [my-exercise.home :as home]))

; This function simplifies the code for adding values
(defn ocdid [id val]
  "Produce the ocd id for a particular val"
  (concat (concat (concat "/" id) ":") (clojure.string/lower-case (clojure.string/replace val #" " "_"))))
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

        (concat (concat (concat (concat (concat (concat "ocd-division/country:us" (ocdid "street" street)) (ocdid "street-2" street-2))) (ocdid "place" city)) (ocdid "state" state)) (ocdid "zip" zip))

        ))

  (route/resources "/")
  (route/not-found "Not found")
)



(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))
