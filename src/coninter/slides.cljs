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

(defn slide-link [slide link-title]
  (w/link "#" link-title))

(defn intro-slide []
  [:div
   [w/card-white
    [w/group-head "Constructing Interfaces"]
    [:h2.mv0.f2.lh-solid "with re-frame"]
    [:p "by " (w/link "https://lyonheart.us" "Matthew Lyon") ". "]
    [:p "These slides are at "
     (w/self-link "https://github.com/mattly/pdx-clojure-constructing-interfaces")
     "."]
    [:p ""]
    [:ul [:li (slide-link coninter.slides.cljs/main "ClojureScript")]]]])

(def slides
  [intro-slide
   coninter.slides.cljs/main
   coninter.slides.cljs/compilers
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
   coninter.slides.reframe/subscription-chains
   coninter.slides.suggestions/main])

(rf/reg-event-db ::init (fn [db _] (merge db {:slide :intro})))

(rf/reg-sub ::current-slide (fn [db _] (:slide db)))


(defn presentation []
  (reagent/create-class
   {:component-will-mount #(rf/dispatch [::init])
    :reagent-render
    (fn []
      (->> slides
           (map vector)
           (interpose [:hr])
           (into [:div.ma4]))
      #_[slide @(rf/subscribe [::current-slide])])}))

; (def highlight (reagent/adapt-react-class js/Highlight))
