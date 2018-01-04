(ns coninter.slides.reagent
  (:require-macros
   [coninter.components.code :refer [defn-with-src defexample example defsrc]])
  (:require
   [cljsjs.react-color]
   [coninter.components.code :as code]
   [coninter.components.widgets :as w]
   [reagent.core :as r]))

(def clojure-link (example [w/link "https://clojure.org" "Clojure"]))

(defn main []
  [:div
   [w/card-white
    [w/group-head "React, Reagent, and components"]
    [:section.mt3
     [w/section-head (w/link "https://reactjs.org" "React")
      " is a JavaScript Library from Facebook for building user interfaces"]
     [:p.mt1 [:strong "It is declarative"]
      " in that your view is ideally the result of a pure function computed"
      " from application state."]
     [:p.mt1 [:strong "It is composable"]
      " in that you can build complex UIs from small, reusable components."]
     [:p.mt1 [:strong "It can be very fast"]
      " if you build things so that only necessary parts update."]
     [:p.mt1 [:strong "It has an odd inline template syntax"]
      " which, while optional, is widely used and requires compiler support."
      " This is because React doesn't really have templates, it has virtual DOM functions."]
     [code/code-file :jsx "welcome.jsx" "function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}"]
     [:p.mt1 " Thankfully, we have something better: data."]
     [:p.mt1 [:strong "It helps you render templates on the server"]
      " though in practice this requires a lot of discipline in your code."]
     [:p.mt1 [:strong "It's just a library"] " and leaves many decisions"
      " about how to fit it into your stack to you. Many people find this"
      " frustrating."]]
    [:section.mt3
     [w/section-head (w/link "http://reagent-project.github.io/" "Reagent")
      " is a 'Minimalistic ClojureScript interface to React'"]
     [:p.mt1 "In their simple forms, components are functions that return hiccup-like html:"]
     [code/code-file :clojure "widgets.cljs" w/link-src]
     [code/hiccup-example clojure-link]]
    [:section.mt3
     [w/section-head "in React parlance, a component is similar to a function"]
     [:ul
      [:li.mt1 "A component is the smallest renderable (or re-renderable) element of the view."]
      [:li.mt1 "A component has lifecycle methods to help your declarative code"
       " manage necessary state or the DOM."]
      [:li.mt1 "A component strives to be composable and resuable."]]]]])

(defn hiccup []
  [:div
   [w/card-white
    [w/slide-head "Reagent's Hiccup and components"]
    [:p.mt1 "Hiccup is a lisp-inspired shorthand for html. It is not a template"
     " language. It is data. Reagent's hiccup differs a bit from the jvm-based"
     " " (w/link "https://github.com/weavejester/hiccup" "weavejester library") ":"]
    [:ul
     [:li.mt1 "A hiccup element is a vector."]
     [:li.mt1 "You'll use a lot of HTML hiccup:"
      [:ul
       [:li.mt1 "An HTML hiccup element's first item is a keyword."
        " It may have " [w/inline-code "#id"] " or " [w/inline-code ".class1.class2"]
        " attributes as part of the keyword: " [w/inline-code ":div#app.bg-white.sans-serif"]]
       [:li.mt1
        "An HTML hiccup element may optionally provide an attributes map as the second item."]
       [:li.mt1
        "all subsequent items in an HTML hiccup element are treated as contents: "
        " either strings or other hiccup."]
       [:li.mt1
        [code/clojure-code
         [:div#app.bg-white.sans-serif {:style {:margin "0"}} "Loading..."]]]]]
     [:li.mt2 "You'll also want to use components:"
      [:ul
       [:li.mt1 "If the first element of the vector is a symbol referencing a function,"
        " reagent will mount the symbol's reference as a component."]
       [:li.mt1 "Any subsequent items in the vector become the arguments to the function."]
       [:li.mt1
        "Here's our link component again:"
        [code/pretty-code :clojure w/link-src]
        "And how we mount it:"
        [code/pretty-code :clojure (:code clojure-link)]]
       [:li.mt1
        "Mounting a component instead of calling a function to return html helps"
        " react give you better performance by only re-rendering the component"
        " when something has changed."]]]
     [:li.mt2 "The only tricky thing are collections."
      [:ul
       [:li.mt1 "React " [:strong "and"] " Reagent will complain about this:"
        [code/clojure-code '[:ul (map (fn [i] [:li i]) (range 10))]]
        [:p.mt0.pa2.bg-light-red.navy
         "react.inc.js:3528 Warning: Each child in an array or iterator should have a unique "key" prop. Check the render method of `coninter.slides.reagent.hiccup`. See https://fb.me/react-warning-keys for more information."]]
       [:li.mt1 "If the collection changes, React has no way to identify the items"
        " in the collection from each other, and has to re-render the whole collection"
        " instead of the individual items that changed."]
       [:li.mt1 "Your first option to avoid the warning is to explicitly materialize"
        " the collection into a container:"
        [code/clojure-code '(->> (range 10) (map (fn [i] [:li i]) (into [:ul])))]
        "While this will avoid the warning, the entire collection still needs to"
        " be re-rendered on a re-render."]
       [:li.mt1 "A better option is to provide a unique identifier for every item:"
        [code/clojure-code '[:ul (map (fn [i] [:li {:key (str i)} i]) (range 10))]]
        "Or you can provide that as metadata:"
        [code/clojure-code '[:p (for [l links] ^{:key l} [footer-link l])]]
        "If `links` changes, it will only re-render items that changed, or "
        " were added or removed."]]]]]])

(defexample click-counter-example
  (comment (require '[reagent.core :as r]))
  (def click-count (r/atom 0))
  (defn click-counter []
    [:div
     [:p "You've made " @click-count " paperclips."]
     [:input {:type "button" :value "Make Paperclip"
              :on-click #(swap! click-count inc)}]])
  [click-counter])

(defexample input-example
  (def first-name (r/atom "You"))
  (def last-name (r/atom "Person"))
  (defn update-from-value [ratom]
    (fn [event] (reset! ratom (.. event -target -value))))
  (defn text-field [ratom]
    (js/console.log :re-rendering ratom)
    [:input {:type :text
             :value @ratom
             :on-change (update-from-value ratom)}])
  (defn display [fname lname]
    [:p "Hello " @fname " " @lname])
  (defn form []
    [:div
     [display first-name last-name]
     [:div.bt.b--light-gray
      [text-field first-name]
      [text-field last-name]]])
  [form])


(defn ratoms []
  [:div
   [w/card-white
    [w/slide-head "Reagent's Reactive Atoms manage state"]
    [:p.mt1 "You should know about these, but don't have to use them. What re-frame"
     " offers instead is much better. But they're useful to know about."]
    [code/hiccup-example click-counter-example]
    [:p.mt1 "Components are functions that get mounted to the DOM, and when"
     " one of those functions de-references a ratom, reagent keeps track of"
     " it, and when the value of that ratom changes, it re-runs the function"
     " with the new value and replaces the segment of the view on the DOM."]]

   [w/card-white
    [:p.mt1 "An advantage of mounting a component as described previously "
     " instead of including its HTML directly is that reagent won't re-render"
     " a component included via a vector unless its arguments change."]
    [code/hiccup-example input-example]]])

(defn lscb [bg js-name cljs-name & details]
  [:tr
   [:td.pa1.bb.b--light-gray {:class bg}
    [:code.f6.purple js-name]
    [:pre [:code.f6.dark-blue (if cljs-name (str cljs-name) "n/a")]]]
   [:td.pa1.bb.b--light-gray {:class bg} (into [:p] details)]])

(defexample timer-example
  (defn time-since-mounted [default-value ms unit-name]
    (let [*timer (r/atom default-value)
          *interval-id (r/atom nil)]
      (r/create-class
       {:component-will-mount (fn [_] (reset! *timer 0))
        :component-did-mount
        (fn [_]
          (reset! *interval-id
                  (js/setInterval #(do (swap! *timer inc)
                                       (js/console.log unit-name))
                                  ms)))
        :component-will-unmount
        (fn [_] (js/clearInterval @*interval-id))
        :reagent-render
        (fn [default-value ms unit-name]
          [:div @*timer " " unit-name])})))
  (defn timers []
    (let [*showing? (r/atom false)
          toggle! (fn [event] (swap! *showing? not))]
      (fn []
        [:div
         [:div [:button {:on-click toggle!}
                (if @*showing? "Unmount Timers" "Mount Timers")]]
         (when @*showing?
           [:div
            [time-since-mounted 0 1000 "seconds"]
            [time-since-mounted 1000 100 "hyperseconds"]])])))
  [timers])


(defn lifecycle []
  [:div
   [w/card-white
    [w/slide-head "Side-Effects and the Component Lifecycle"]
    [:p.mt1 "Often times you'll need to setup some kind of state when a"
     " component is mounted, or perform side-effects when it changes. React offers"
     " lifecycle callbacks for components:"]
    [:p.mt1 "React components have a notion of " [:strong "props"] ", or"
     " externally-managed state, and " [:strong "state"] ", or locally-managed"
     " state. Reagent ignores React's local state - which requires working with"
     " a javascript-specfic API to pretend that js-mutable data is immutable."
     " Instead, supplemental items in a mounted-component's vector become props."]
    [:table.collapse
     [:thead
      [:tr
       [:td.bb.b--light-gray "React callback, Reagent equiv"]
       [:td.bb.b--light-gray "Arguments"]
       [:td.bb.b--light-gray "Notes"]]]
     [:tbody
      [lscb :bg-light-silver "constructor" nil  "unused - included with cljs functions"]
      [lscb :bg-light-gray "componentWillMount" :component-will-mount
       "ran before the component is rendered and mounted. You can't block for any"
       " side-effects, and the DOM isn't available, so it's not useful in Reagent"]
      [lscb nil "componentDidMount" :component-did-mount
       "ran after the component is rendered and mounted. Useful for side-effects and DOM manipulation."]
      [lscb :bg-moon-gray "componentWillReceiveProps" :component-will-receive-props
       "Given 'next-props', you can update a component's local state. May be called"
       " multiple times, so it shouldn't side-effect. You shouldn't have to use this."]
      [lscb :bg-moon-gray "shouldComponentUpdate" :should-component-update
       "Given 'old-state' and 'new-state', determines if the component should "
       " re-render. Since reagent ignores component-local state,"
       " you can ignore this."]
      [lscb :bg-light-gray "componentWillUpdate" :component-will-update
       "called before a mounted component is re-rendered. You can't block for "
       " side-effects, so it's not that useful in Reagent."]
      [lscb nil "componentDidUpdate" :component-did-update
       "called after a mounted component is re-rendered. Useful for side-effects"
       " and DOM manipulation."]
      [lscb nil "componentWillUnmount" :component-will-unmount
       "called before a mounted component is unmounted and disposed of. Useful for"
       " side effects."]
      [lscb :bg-light-red "render" :render
       "Reagent exposes this, but don't use it. You'll cause subtle bugs."]
      [lscb nil "n/a" :reagent-render
       "The normally implicit function that renders your view."]]]
    [:p.mt1 "In nearly five years of working with React, I've used these callbacks"
     " for a wide variety of things, some good, some horrific. Since moving to"
     " clojurescript, mostly good."]
    [:p.mt1 "Justified use of callbacks include:"]
    [:ul
     [:li.mt1 "Initializing state from external sources (ajax, browser, etc)"]
     [:li.mt1 "Initializing global event listeners to for things your component might"
      " need to update for. For example, window.resize."]
     [:li.mt1 "Gathering information about the DOM elements you're working with,"
      " for example, getting the size of an element to draw a chart in."]
     [:li.mt1 "working with JS libraries that assume the presence of a DOM element (see refs, next slide)"]]
    [code/hiccup-example timer-example]]])

(defn refs []
  [:div
   [w/card-white
    [w/slide-head "Accessing the DOM directly with refs"]
    [:p.mt1 "In React parlance, a " [:strong "ref"] " is a reference to a element"
     " rendered from your view that has been mounted and is present in the"
     " browser's current document."]
    [:p.mt1 "They're provided as an escape hatch for when you need to imperatively"
     " work with the DOM outside of the typical data flow."]
    [:p.mt1 "They should be used sparingly. Some good uses:"]
    [:ul
     [:li.mt1 "Managing focus, text selection, or media playback"]
     [:li.mt1 "Triggering imperative animations"]
     [:li.mt1 "Integrating with third-party libraries that imperatively work with the DOM."]]
    [:p.mt1 "Refs should not be a way to " [:em "own"] " state that belongs"
     "elsewhere. Use them sparingly."]
    [code/code-file :clojure "components/code.cljs" code/pretty-code-src]]])

(defsrc cljsjs-example
  (comment (require '[cljsjs.react-color])
    "note the lack of :as")
  (def swatch-picker (r/adapt-react-class js/ReactColor.SwatchesPicker))
  (defn color-info [*info]
    [:div.mt3]
    (if-let [info @*info]
      [:p "You chose: " (.-hex info)]
      [:p "Choose a color."]))
  (defn color-swatches [picker-component]
    (let [*mycolor (r/atom nil)]
      (fn []
        [:div
         [swatch-picker {:on-change (fn [color event] (reset! *mycolor color))}]
         [color-info *mycolor]]))))

(defn community []
  [:div
   [w/card-white
    [w/slide-head "Adapting Community Components"]
    [:p.mt3 "React has a vibrant community and there are a lot of components"
     " available out there, both via "
     [w/link "http://cljsjs.github.io/" "cljsjs"]
     " and " [w/link "https://npmjs.org" "npm"] ". Let's look at how to use"
     " components from either."]
    [:p.mt4 "cljsjs has a lot of pre-adapted javascript libraries. The advantage"
     " is that they're already optimized for the closure compiler. The disadvantage"
     " is that it's not often obvious how to use them."]
    [code/clojure-code-file "in project.clj's :dependencies" '[cljsjs/react-color "2.13.1-0"]]
    [code/code-file :clojure "cljsjs example" cljsjs-example]
    [color-swatches]
    [:p.mt1 "since cljsjs modules are loaded into the js/ namespace, and many"
     " libraries' documentation these days assumes an ES201*-style import"
     " statement. I had to poke around at the JavaScript console before finding"
     " " [w/inline-code "ReactColor"] " as the entry-point."]
    [:p.mt4 "I wanted to show off using a community component via npm, but"
     " I couldn't actually get it working."]]])
