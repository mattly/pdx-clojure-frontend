(ns coninter.slides.reframe
  (:require-macros
   [coninter.components.code :refer [defsrc]])
  (:require
   [clojure.string :as str]
   [coninter.components.code :as code]
   [coninter.components.widgets :as w]
   [re-frame.core :as rf]
   [reagent.core :as r]))

(defn main []
  [:div
   [w/card-white
    [w/group-head "Re-Frame: Event and State Management"]
    [:p.mt1 [w/link "https://github.com/Day8/re-frame" "re-frame"]
     " is a clojurescript framework for managing state on top of reagent components."
     " It combines " [:strong "immutable data"] " with " [:strong "functional programming"]
     " and " [:strong "reactive programming"] " to give a great development experience"
     " for front-end programming."
     " It is similar in notion to " [w/link "https://redux.js.org/" "redux"] " and "
     [w/link "http://elm-lang.org/" "Elm"] ", though predates both. They've all"
     " inspired each other somewhat as they evolve."]]
   [w/card-white
    [:p.mt1 "Being a framework, re-frame encourages a certain amount of buy-in"
     " in order to make the best use of it. Instead of managing a disparate collection"
     " of reactive atoms, it uses a single, global map to store state for your"
     " entire application. In turn, you only have one application."]
    [:p.mt1 "This causes some issues that make it difficult to use with"
     " Figwheel developer Bruce Hauman's " [w/link "https://github.com/bhauman/devcards" "devcards"]
     " library, which aims to provide a visual repl for developing components."
     " It's not impossible, but requires a lot of discipline in order to make"
     " it work well."]
    [:p.mt1 "Personally, I think it's worth the trade-off, but if you'd rather stick"
     " to something that's more of a library, and something that mirrors the react"
     " model of state and props a bit more closely, it's worth checking out "
     [w/link "https://github.com/tonsky/rum/" "Rum"] ", which is an alternate"
     " React wrapper and " [w/link "https://github.com/roman01la/citrus" "Citrus"]
     " which provides a less-global-ish state management paradigm. I haven't used"
     " either yet, so if you do, please let me know what you think."]]
   [w/card-white
    [:div.dib.w-40
     [:img {:src "https://github.com/Day8/re-frame/raw/master/images/Readme/Dominoes-small.jpg?raw=true"}]]
    [:div.dib.w-60.pl3
     [:p "re-frame has what it calls a six-domino cascade:"]
     [:ol
      [:li.mt1 [:strong "Event Dispatch"]
       " - something happens in the browser - user clicks, js callbacks, etc"]
      [:li.mt1 [:strong "Event Handling"]
       " - putting the event into re-frame's system and determining what to do next."
       " The re-frame API confusingly calls 'dispatch'"]
      [:li.mt1 [:strong "Effect Handling"]
       " - triggering AJAX calls, location bar updates, etc"]
      [:li.mt1 [:strong "Query"]
       " - reading from your application state"]
      [:li.mt1 [:strong "View Rendering"]
       " - this is the reagent stuff we just covered"]
      [:li.mt1 [:strong "DOM updates"]
       " - this is handled by React for you"]]]]])


(defsrc simple-loop-src
  (comment (require '[re-frame.core :as rf]))
  (defn paperclips-count [db _] (or (:paperclips/count db) 0))
  (rf/reg-sub :paperclips/count paperclips-count)
  (defn make-paperclip [db _] (update db :paperclips/count inc))
  (rf/reg-event-db :paperclip/make make-paperclip)
  (defn paperclips []
    [:div
     [:p "Paperclips: " @(rf/subscribe [:paperclips/count])]
     [:button {:on-click #(rf/dispatch [:paperclip/make])} "Make Paperclip"]]))

(def sub-1
  [w/card-white
   [w/slide-head "Simple Subscriptions and Events"]
   [:p.mt1 "This example shows off a simple loop:"]
   [code/code-file :clojure "subscription :paperclips" simple-loop-src]
   [paperclips]
   [:p.mt3 "It defines:"]
   [:ul
    [:li.mt1 "A subscription that pulls out the " [w/inline-code ":paperclips/count"]
     " value from the application state"]
    [:li.mt1 "An event handler to increment that value"]
    [:li.mt1 "A view, which displays the materialized value of the subscription,"
     " and provides a button which, in the JavaScript event, triggers a dispatch"
     " of the event handler."]]
   [:p.mt1 "There is no outside state or side effects, and the way I've constructed"
    " this, both the value-get and value-update functions are available elsewhere"
    " to the source."]])

(defsrc whole-state-src
  (rf/reg-sub :whole-state (fn not-identity [db _] db))
  (defn whole-state []
    [code/code-file :clojure "whole application state" @(rf/subscribe [:whole-state])]))

(defsrc bad-paperclips-src
  (rf/reg-sub :non-present-value :theres-nothing-here)
  (defn bad-paperclips-sub []
    [code/clojure-code @(rf/subscribe [:non-present-value])]))

(def sub-2
  [w/card-white
   [:p.mt1 "You can use inline functions if you want:"]
   [code/code-file :clojure "whole-state" whole-state-src]
   [whole-state]
   [:p.mt1 "But remember that the subscription function takes two arguments,"
    " and both are provided to the IFn implementation on keyword:"]
   [code/code-file :clojure "bad subscription example" bad-paperclips-src]
   [bad-paperclips-sub]])

(defsrc argument-example-src
  (defn counter-value [db [_sub-id counter-id]]
    (get-in db [:counters counter-id] 0))
  (rf/reg-sub :counter counter-value)
  (defn counter-update [db [_dispatch-id counter-id]]
    (update-in db [:counters counter-id] inc))
  (rf/reg-event-db :counter-inc counter-update)
  (defn counter [id]
    (let [*value (rf/subscribe [:counter id])
          inc! #(rf/dispatch [:counter-inc id])]
      [:button.mr2 {:on-click inc!} id ": " @*value])))

(def sub-3
  [w/card-white
   [:p.mt-1 "The second argument to the functions provided to "
    [w/inline-code "rf/reg-sub"] " and " [w/inline-code "rf/reg-event-db"]
    " are the vector provided via " [w/inline-code "rf/subscribe"] " and "
    [w/inline-code "rf/dispatch"] ", repsectively:"]
   [code/code-file :clojure "subscriptions and events with arguments" argument-example-src]
   [:div [counter :one] [counter :two] [counter :three]]])

(defn subscriptions-simple []
  [:div sub-1 sub-2 sub-3])


(defsrc bad-side-effect-src
  (rf/reg-event-db
   :bad-alert
   (fn [db [_ msg]]
     (js/alert (str "You said: " msg))
     (update db :alert/bad-count inc)))
  (defn bad-alert-button []
    [:button {:on-click #(rf/dispatch [:bad-alert "Derp"])} "Derp!"]))

(defsrc good-side-effect-src
  (rf/reg-fx
   :alert
   (fn [msg] (js/alert msg)))
  (rf/reg-event-fx
   :good-alert
   (fn [{:keys [db]} [_ msg]]
     {:alert msg
      :db (update db :alert/good-count inc)}))
  (defn good-alert-button []
    [:button {:on-click #(rf/dispatch [:good-alert "Alerts are annoying!"])}
     "Hey! Listen!"]))

(defsrc log-fx-src
  (rf/reg-fx :log (fn [msgs] (apply js/console.log msgs))))

(defsrc ajax-src
  (defn ajax-faker [{:keys [succeed? on-complete]}]
    (js/setTimeout on-complete 5000 [succeed? {:value 42}]))
  (rf/reg-fx
   :ajax
   (fn [{:as opts :keys [success failure]}]
     (ajax-faker
      (-> opts
          (dissoc :success :failure)
          (assoc :on-complete
                 (fn [[ok? result]]
                   (rf/dispatch (conj (if ok? success failure) result))))))))
  (rf/reg-event-db
   :ajax/success
   (fn [db [_ result]]
     (merge db {:ajax/state :complete :ajax/result result})))
  (rf/reg-event-fx
   :ajax/failure
   (fn [{:keys [db]} [_ result]]
     {:log [:ajax/failed result]
      :db (assoc db :ajax/state :failed)}))
  (rf/reg-event-fx
   :make-ajax-call
   (fn [{:keys [db]} [_ succeed?]]
     (when-not (= :loading (:ajax/state db))
       {:db (assoc db :ajax/state :loading)
        :ajax {:success [:ajax/success]
               :failure [:ajax/failure]
               :succeed? succeed?}})))
  (rf/reg-sub :ajax/state (fn [db] (:ajax/state db)))
  (rf/reg-sub :ajax/result (fn [db] (:ajax/result db)))
  (defn ajaxy []
    (let [*state (rf/subscribe [:ajax/state])]
      (fn []
        [:div
         [:button.mr3 {:on-click #(rf/dispatch [:make-ajax-call false])
                       :disabled (= :loading @*state)}
          "Make failing ajax call"]
         [:button.mr3 {:on-click #(rf/dispatch [:make-ajax-call true])
                       :disabled (= :loading @*state)}
          "Make successful ajax call"]
         (when (= :loading @*state) [:span.mid-gray "Loading..."])
         (when (= :complete @*state)
           [code/clojure-code @(rf/subscribe [:ajax/result])])]))))

(defn fx-ex [& src]
  (->> src
      (map (fn [s] [code/clojure-code s]))
      (into [:div.bb.b--light-gray.pv1])))
(defn fx-notes [n] [:div.pv1.mb3 n])

(defn dispatch-events-with-fx []
  [:div
   [w/card-white
    [w/slide-head "Declarable Side-effects"]
    [:p.mt1 "Sometimes we need to do something outside of our state map."
     " We " [:em "could"] " use a side-effecting function in an event handler"
     " but that's difficult to test. Don't do this:"]
    [code/code-file :clojure "bad alert example" bad-side-effect-src]
    [bad-alert-button]
    [:p.mt3 "Use " [w/inline-code "reg-fx"] " and "
     [w/inline-code "reg-event-fx"] " instead:"]
    [code/code-file :clojure "good alert example" good-side-effect-src]
    [good-alert-button]
    [:p.mt3 "Having an event handler function return a map of effects to"
     " dispatch is more in the functional philosophy, helps you write more"
     " easily testable code, and encourages you to consolidate the side-effecting"
     " points in your code. "]
    [:p.mt1 [w/inline-code "reg-fx"] " is good for ajax calls, logging, updating"
     " the browser's location bar or local storage, talking over a websocket,"
     " or anything else besides updating the app's database that needs to"
     " happen in reaction to an event being dispatched."]
    [:p.mt1 "Seriously, you have no excuse:"]
    [code/code-file :clojure "declarative logging" log-fx-src]
    [:p.mt1 "Managing callback chains can be a bit difficult at times. This is"
     " a pattern I've arrived at for doing ajax calls:"]
    [code/code-file :clojure "ajax callback pattern" ajax-src]
    [ajaxy]]
   [w/card-white
    [:p.mt1 "re-frame includes a bunch of built-in effect handlers:"]
    [fx-ex '{:db {:initialized? true}}]
    [fx-notes "Resets app-state map with a new (or modified) value"]
    [fx-ex '{:dispatch [:paperclip/make]}]
    [fx-notes "dispatches a single event, expects a single vector"]
    [fx-ex '{:dispatch-n [[:paperclip/reset]
                          [:paperclip/make]]}]
    [fx-notes "dispatches multiple events, in order"]
    [fx-ex '{:dispatch-later [{:ms 100 :dispatch [:paperclip/make]}
                              {:ms 200 :dispatch [:paperclip/make]}]}]
    [fx-notes "dispatches one or more events after a specified delay"]
    [fx-ex '{:deregister-event-handler :paperclip/reset}
           '{:deregister-event-handler [:paperclip/make :paperclip/reset]}]
    [fx-notes "You can dynamically register event handlers, and this is how you get rid of them"]]])

(defsrc cofx-setup
  (rf/reg-cofx
   :now
   (fn [cofx _]
     (assoc cofx :now (js/Date.))))
  (rf/reg-cofx
   :random-int
   (fn [cofx int-till]
     (assoc cofx :random-int (rand-int int-till))))
  (rf/reg-event-fx
   :update-my-cofx
   [(rf/inject-cofx :now) (rf/inject-cofx :random-int 10)]
   (fn [{:as cofx :keys [db now random-int]} _]
     {:db (assoc db :cofx {:now now :rando random-int})}))
  (rf/reg-sub
   :my-cofx-sub
   (fn [db] (:cofx db)))
  (defn cofx-display []
    (r/create-class
     {:component-did-mount #(rf/dispatch [:update-my-cofx])
      :reagent-render
      (fn []
        (if-let [cofx-data @(rf/subscribe [:my-cofx-sub])]
          [code/clojure-code cofx-data]
          [:p "Loading..."]))})))

(defsrc window-size-src
  (rf/reg-cofx
   :window-size
   (fn [cofx _]
     (assoc cofx :size {:width js/document.body.clientWidth
                        :height js/document.body.clientHeight})))
  (rf/reg-event-fx
   :update-window-size
   [(rf/inject-cofx :window-size)]
   (fn [{:as cofx :keys [db size]}]
     {:db (assoc db :window/size size)}))
  (rf/reg-sub :window-size (fn [db] (:window/size db)))
  (defn window-size []
    (r/create-class
     (let [listener #(rf/dispatch [:update-window-size])]
       {:component-will-mount
        #(do (listener)
             (js/window.addEventListener "resize" listener))
        :component-will-unmount
        #(js/window.removeEventListener "resize" listener)
        :reagent-render
        (fn []
          (if-let [size @(rf/subscribe [:window-size])]
            [:p (:width size) "px by " (:height size) "px"]
            [:p "Loading..."]))}))))

(defn pull-outside-state-with-cofx []
  [:div
   [w/card-white
    [w/slide-head "Declaring Coeffects, and Interceptors"]
    [:p.mt1 "Sometimes you need to rely on outside state in your event handlers:"
     " window.location, localStorage, window's size, etc. Your effect handlers"
     " can declare they want these as dependencies, if you setup an appropriate"
     " cofx interceptor:"]
    [code/code-file :clojure "an example of a cofx chain" cofx-setup]
    [cofx-display]
    [:p.mt3 "The re-frame docs offer examples of overwriting a cofx interceptor"
     " with a fixture during a test, so you can easily stub out f.e. js/Date."]]
   [w/card-white
    [:p.mt1 "Here's an example that comes in useful when I need to redraw f.e."
     " a graph based on the width of the window or a dynamically-sized element."]
    [:p.mt1 "In real use, you'd probably want to make `listener` a top-level def"
     " so that the browser only registers it once, and perhaps have some state"
     " management around only removing the listeneer when all subscribers have"
     " unmounted."]
    [code/code-file :clojure "A window resize cofx and event listener setup"
     window-size-src]
    [window-size]]])

(defsrc debug-example
  (rf/reg-event-db
   :paperclips/reset
   [rf/debug]
   (fn [db _]
     (assoc db :paperclips/count 0)))
  (defn reset-clips []
    [:div
     [:div.dib.mr2 "Paperclips: " @(rf/subscribe [:paperclips/count])]
     [:button.mr2 {:on-click #(rf/dispatch [:paperclip/make])} "Make!"]
     [:button {:on-click #(rf/dispatch [:paperclips/reset])} "Start Over"]]))

(defsrc interceptor-example
  (defn simple-dispatcher [input interceptors]
    (as-> input $
      (reduce (fn [x {:keys [before]}] (before x)) $ (filter :before interceptors))
      (reduce (fn [x {:keys [after]}] (after x)) $ (filter :after (reverse interceptors)))))
  (comment (simple-dispatcher x [j i h g f])))

(defn interceptors []
  [:div
   [w/card-white
    [:p.mt1
     "Ultimately, effects and coeffects in re-frame follow the "
     [:strong "interceptor"] " pattern. An interceptor is a wrapper around"
     " something else. Think middleware, but instead of each item calling"
     " the next item in the stack directly, interceptors are a collection"
     " that is reduced by an outside dispatcher."]
    [code/clojure-code-file "middleware composition" '(f (g (h (i (j x)))))]
    [code/code-file :clojure "interceptor composition" interceptor-example]
    [:p.mt1 "As a lisper, of course you already understand the power of data as code."
     " But if you don't, handling dispatch this way allows each interceptor"
     " function to focus solely on what it needs to do, and worry about leaving"
     " the invocation of the rest of the chain up to code that can worry solely"
     " about that."]
    [:p.mt1 "Ultimately, " [w/inline-code "reg-event-db"] " is sugar around"
     " using an interceptor to hoist the " [w/inline-code ":db"] " key out"
     " of the context of the event handler, so you don't have to worry about"
     " irrelevant things."]
    [:p.mt1 "Building your own interceptor is beyond the scope of this talk,"
     " and in two years of working with re-frame I have yet to justly need to"
     " create my own."
     " re-frame does include a few by default, and by far and away the most"
     " useful of these is " [w/inline-code "debug"] ":"]
    [code/code-file :clojure "Demo use of debug - open console"
     debug-example]
    [reset-clips]]])

(defsrc chain-example
  (rf/reg-event-db :randos/populate
                   (fn [db [_ & randos]]
                     (js/console.log randos)
                     (assoc db :randos {:values randos :scale [1 2 3]})))
  (rf/reg-event-db :randos/scale (fn [db [_ scale]] (assoc-in db [:randos :scale] scale)))
  (rf/reg-sub :randos/values
              (fn [db]
                (js/console.log :randos/values (-> db :randos :values))
                (-> db :randos :values)))
  (defn rando-values [] (rf/subscribe [:randos/values]))
  (rf/reg-sub :randos/nth rando-values
              (fn [values [_ n]]
                (js/console.log :randos/nth n)
                (nth values n)))
  (rf/reg-sub :randos/length rando-values count)
  (rf/reg-sub :randos/third-times-length
              (fn [_] [(rf/subscribe [:randos/nth 2])
                       (rf/subscribe [:randos/length])])
              (fn [[third length]] (* third length)))
  (rf/reg-sub :randos/scale (comp :scale :randos))
  (rf/reg-sub :randos/scale-matrix
              (fn [_] [(rando-values) (rf/subscribe [:randos/scale])])
              (fn [[values scales]]
                (js/console.log :randos/scale-matrix)
                (map (fn [scale] (map #(* scale %) values)) scales)))
  (rf/reg-sub :randos/scale-matrix-markup #(rf/subscribe [:randos/scale-matrix])
              (fn [matrix]
                (->> matrix
                     (map (partial map (partial conj [:td])))
                     (map (partial into [:tr]))
                     (into [:tbody])
                     (conj [:table]))))
  (defn seed-randos [js-event]
    (js/console.log :seeding-randos-clicked)
    (->> (rand-int 10)
         (range)
         (mapv (fn [_] (rand-int 10)))
         (into [:randos/populate])
         rf/dispatch))
  (defn rando-details []
    [:ul
     [:li "Values are: " (str/join ", " @(rf/subscribe [:randos/values]))]
     [:li "Third value times length is: " @(rf/subscribe [:randos/third-times-length])]])
  (defn rando-scale []
    [:ul
     [:li "The scale is: " (str/join ", " @(rf/subscribe [:randos/scale]))]
     [:li @(rf/subscribe [:randos/scale-matrix-markup])]])
  (def scale-buttons
    (map (fn [[ffn label]] [(->> (range) (filter ffn) (take 5)) label])
         [[odd? "Odds"] [even? "Even"]
          [#{0 1 2 3 5 8 13 21 34} "Fibonacci"]
          [#{2 3 5 7 11 13 17 19} "Prime"]]))
  (defn rando-main []
    [:div [rando-details] [rando-scale]
     [:div [:button.mr2 {:on-click seed-randos} "Make Randos"]
      (doall
        (for [[svals label] scale-buttons]
          [:button.mr2 {:key label :on-click #(rf/dispatch [:randos/scale svals])}
           "scale: " label]))]]))

(defn subscription-chains []
  [:div
   [w/card-white
    [w/slide-head "Derived Values, Flowing"]
    [w/section-head "Or, Subscriptions Subscribing to Subscriptions"]
    [:p.mt1 "Circling back to the query portion of our cycle, if you have a lot"
     " of different subscriptions, it can get expensive to call each of them"
     " every time any part of the app-state map changes. "]
    [code/code-file :clojure "subscrpition chain example" chain-example]
    [rando-main]]])
