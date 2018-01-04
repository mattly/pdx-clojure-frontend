(ns coninter.slides.front-end
  (:require
   [coninter.components.widgets :as w]))

(defn main []
  [:div
   [w/card-white
    [w/slide-head "What's Different About Front-end Development?"]
    [:p.mt3 "Programming for something on the server typically looks like this:"]
    [:ul
     [:li.mt1 "Something happened that you need to respond to. A request, a queue item, etc."]
     [:li.mt1 "You hydrate any state not included in the event from a cache or database or somewhere."]
     [:li.mt1 "You do whatever processing you need to do."]
     [:li.mt1 "You persist any new state that needs to be persisted"]
     [:li.mt1 "Maybe you respond to the event if needed, or trigger other side-effects."]]
    [:p.mt1 "This typically happens on a computer you control, with lots of layers"
     " of control around making sure the event is properly-formed and handled"
     " in a kind of queue, to make sure you're making the best use of your"
     " resources, and events are coordinated and dispatched properly."]
    [:p.mt1 "You can take a lot for granted, and to a certain point of scaling,"
     " I'd consider it 'easy mode'. Making it so is I think the point of all the"
     " infrastructure tooling that's become prevalent lately."]
    [:p.mt4 "In contrast, programming for a user interface involves:"]
    [:ul
     [:li.mt1 "You're running on a device you don't own or control."]
     [:li.mt1 "In the browser, you don't even have control of the runtime."]
     [:li.mt1 "You might have to ask the user permission to do some thigns you"
      " want to do, such as get their location or send them notifications."
      " If they decline those permissions, you have to deal with it."]
     [:li.mt1 "You're often sharing the limited memory and processing resources"
      " of the user's computer with other things."]
     [:li.mt1 "You can't assume good network access."]
     [:li.mt1 "You're dealing with a lot of disparate events from one place:"
      " user clicks, keystrokes, window resizes, ajax responses, network"
      " offline/online notices, etc."]
     [:li.mt1 "In the browser, you're doing this in a single thread."]
     [:li.mt1 "Some of these events come from an unpredictable and easily-frustrated user."]
     [:li.mt1 "Time becomes important in figuring how some of the events relate"
      " to each other."]
     [:li.mt1 "State management is entirely up to you."]
     [:li.mt1 "Persisting state locally and remotely is entirely up to you."]
     [:li.mt1 "Translating state changes to view changes is entirely up to you."]
     [:li.mt1 "To compound these problems, your user interface is the " [:em "face"]
      " of your product that people see. If it behaves poorly and unpredictably,"
      " people are much less forgiving than they are if you communicate errors"
      " properly. Instagram survived day-long server outages, Twitter survived the"
      " failwhale, but I don't think either could have survived a janky interface."]]
    [:p.mt1 "So, no pressure or anything."]]])
