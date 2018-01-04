(ns coninter.slides.suggestions
  (:require
   [coninter.components.widgets :as w]))

(defn main []
  [:div
   [w/card-white
    [w/slide-head "Some sugggestions from the trenches"]]])

;; * model logic as state machines (plural) as much as possible,
;;   store your app state accordingly
;; * name all functions
;; * use all tools available if you need to:
;;   - if I need expensive or difficult-to-get "derived" state in a javascript
;;     event or a re-frame event handler, I pass an atom to the subscription
;;     that produces that state, store the values in it, make sure the subscription
;;     gets dereferenced in the view that triggers the event, and then pass the
;;     atom to the events
;; * event handlers and data queries (subscriptions) form a graph. Don't be afraid to diagram that if needed
