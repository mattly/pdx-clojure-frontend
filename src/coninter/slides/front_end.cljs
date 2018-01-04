(ns coninter.slides.front-end
  (:require
   [coninter.components.widgets :as w]))

(defn main []
  [:div
   [w/card-white
    [w/slide-head "What's Different About Front-end Development?"]]])

;; * dealing with the user's device
;; * dealing with the user's behavior
;; * unreliable network
;; * lots of disparate events
;; * interrelation of state between these events
;; * translating that state to the view
;; * limited resources
