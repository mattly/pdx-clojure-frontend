(ns coninter.slides.suggestions
  (:require-macros
   [coninter.components.code :refer [defsrc]])
  (:require
   [coninter.components.code :as code]
   [coninter.components.widgets :as w]
   [re-frame.core :as rf]))

(defsrc state-machine-src
  (comment "bad")
  (rf/reg-event-db
   :state-as-boolean
   (fn [db _]
     (assoc-in db [:my-thing :loading?] true)))
  (comment "better")
  (rf/reg-event-db
   :state-as-keyword
   (fn [db _]
     (assoc-in db [:my-thing :state] :loading))))

(defsrc raw-access-src
  (rf/reg-event-db
   :assoc-in
   (fn [db [_ path value]]
     (assoc-in db path value)))
  (rf/reg-sub
   :get-in
   (fn [db [_ path fallback]]
     (get-in db path fallback))))

(defn main []
  [:div
   [w/card-white
    [w/slide-head "Some sugggestions from the trenches"]
    [:p.mt1 "Regardless of what you're working in or for, these are some hard-"
     "won points I think are applicable to all front-end programming."]
    [:ul
     [:li.mt1 "Model logic and your data structure as a collection of finite"
      " state machines as much as you possibly can."
      [code/code-file :clojure "explicit state flow" state-machine-src]
      [:p.mt1 "I've come to favor collections of small, simple state machines"
       " you'll use properly over a single large, complex one you won't."
       " Some good reading: "
       [w/link "https://seabites.wordpress.com/2011/12/08/your-ui-is-a-statechart/"
               "Your UI is a statechart"]
       " and "
       [w/link "http://blog.cognitect.com/blog/2017/8/14/restate-your-ui-creating-a-user-interface-with-re-frame-and-state-machines"
        "Cognitect series on re-frame and state machines"]]]
     [:li.mt3 "Figure out how to fail gracefully early in the project's life."
      " Janky interfaces don't just scare away users. They also cause stakeholders"
      " to come to you whenever anything, anywhere goes wrong in a way you"
      " didn't plan for."]
     [:li.mt3 "How events translate into state changes and side effects are"
      " form a graph. How queries against the state become the view also form"
      " a graph. Diagrams and documentation are your friends."]]
    [:p.mt1 "And some particular ideas useful for working with re-frame"]
    [:ul
     [:li "favor defined functions over inline ones, and if you have to use"
      " an inline function, favor a named one over an anonymous one."]
     [:li.mt3 "Embrace the repository. It's really tempting to do this:"
      [code/code-file :clojure "bad state access" raw-access-src]
      [:p.mt1 "Do yourself a favor and don't. Re-frame encourages you to"
       " create defined entry points into modifying and querying state for a"
       " handful of reasons. By side-stepping that as above, you're tying"
       " the implementation of your state's structure to your event handlers"
       " and view queries, and making the surrounding code a lot more brittle."]]
     [:li.mt3 "Use clojure's other tools as necessary for things re-frame isn't good at."
      [:p.mt1 "For example, if you hae derived state from a subscription and"
       " need to use it in a javascript event or a dispatched event, there's"
       " no easy way to get at that."]
      [:p.mt1 "You have to either recompute it in a dispatched"
       " event handler, which could be complex to reconstruct and expensive to"
       " recompute, or you have to pass it to your javascript event in the view,"
       " which means generating a new event function, and redrawing the view."]
      [:p.mt1 "A solution I came up with for this, that felt kludgy at the"
       " time but less so since, is to use clojure's atoms as a way to pass"
       " around a reference to something that can change. Place them into your"
       " javascript event, into your re-frame dispatcher, and into a needed"
       " subscription, and make the value of the atom the value of the"
       " subscription and then dereference the value in the view that needs"
       " the event. Then, dereference the atom when needed in the events."]]]]])
     
