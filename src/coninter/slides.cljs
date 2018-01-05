(ns coninter.slides
  (:require
   [clojure.string :as str]
   [coninter.components.widgets :as w]
   [coninter.slides.cljs]
   [coninter.slides.front-end]
   [coninter.slides.reagent]
   [coninter.slides.reframe]
   [coninter.slides.suggestions]
   [re-frame.core :as rf]
   [reagent.core :as reagent]))

(defn intro-slide []
  [:div
   [w/card-white
    [w/group-head "Constructing Interfaces"]
    [:h2.mv0.f2.lh-solid "with re-frame"]
    [:p "by " (w/link "https://lyonheart.us" "Matthew Lyon") ". "]
    [:p "These slides are at "
     (w/self-link "https://github.com/mattly/pdx-clojure-frontend")
     "."]
    [:p ""]]])

(def slides
  [intro-slide
   coninter.slides.cljs/main
   coninter.slides.cljs/compilers
   coninter.slides.cljs/hot-reloading-repl
   coninter.slides.cljs/main-differences
   coninter.slides.cljs/js-libs
   coninter.slides.cljs/cljc
   coninter.slides.front-end/main
   coninter.slides.reagent/main
   coninter.slides.reagent/hiccup
   coninter.slides.reagent/ratoms
   coninter.slides.reagent/lifecycle
   coninter.slides.reagent/refs
   coninter.slides.reagent/community
   coninter.slides.reframe/main
   coninter.slides.reframe/subscriptions-simple
   coninter.slides.reframe/dispatch-events-with-fx
   coninter.slides.reframe/pull-outside-state-with-cofx
   coninter.slides.reframe/interceptors
   coninter.slides.reframe/subscription-chains
   coninter.slides.suggestions/main])

(rf/reg-cofx :location/hash
             (fn [c] (assoc c :hash js/window.location.hash)))

(rf/reg-event-fx
 ::init
 [(rf/inject-cofx :location/hash)]
 (fn [{:keys [db hash]} _]
   (let [hash-no (js/parseInt (re-find #"\d+" hash))
         new-db (merge {:slide/number hash-no} db)]
     {:db new-db
      :set-hash (:slide/number new-db)})))

(rf/reg-sub ::current-slide
            (fn [db _]
              (when-let [n (:slide/number db)]
                (nth slides n))))

(rf/reg-fx :set-hash
           (fn [num]
             (js/history.pushState nil "" (str "#" num))))

(rf/reg-event-fx
 :nav/go
 (fn [{:keys [db]} [_ modfn]]
   (let [next-slide-number (modfn (:slide/number db))]
     (when (and (< next-slide-number (count slides))
                (< -1 next-slide-number))
       {:db (assoc db :slide/number next-slide-number)
        :set-hash next-slide-number}))))

(defn forward-arrow-keys [dispatch-v]
  (fn [event]
    (when-let [dir (get {"ArrowLeft" dec "ArrowRight" inc} event.code)]
      (.preventDefault event)
      (rf/dispatch (conj dispatch-v dir)))))

(defn presentation []
  (let [listener (forward-arrow-keys [:nav/go])]
    (reagent/create-class
     {:component-will-mount
      (fn [_]
        (js/window.addEventListener "keydown" listener)
        (rf/dispatch [::init]))
      :component-will-unmount
      (fn [_] (js/window.removeEventListener "keydown" listener))
      :reagent-render
      (fn []
        [:div.ma4
         (when-let [slide @(rf/subscribe [::current-slide])]
           [slide])])})))

; (def highlight (reagent/adapt-react-class js/Highlight))
