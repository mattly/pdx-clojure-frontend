(ns coninter.slides.cljs
  (:require-macros
   [coninter.components.code :refer [defexample resource-content defsrc]])
  (:require
   [cljs-time.core :as time]
   [cljs-time.format :as time.fmt]
   [coninter.components.code :as code]
   [coninter.components.widgets :as w]
   [goog.crypt :as crypt]
   [goog.i18n.NumberFormat.Format :as num.formats]
   [goog.Uri :as Uri])
  (:import
    goog.crypt.Sha256
    goog.date.Date
    goog.date.Interval
    goog.i18n.NumberFormat
    goog.Uri))


(defexample goog-date-example
   (comment
    "when not in the repl, must be in the `ns` form"
    (import 'goog.date.Date)
    (import 'goog.date.Interval))
   (let [d (Date. 2018 0 4)]
     (.add d (Interval. 0 4))
     (.toIsoString d)))

(defexample cljs-time-example
  (comment
   "when not in the repl, must be in the `ns` form"
   (require '[cljs-time.core :as time])
   (require '[cljs-time.format :as time.fmt]))
  (time.fmt/unparse
   (time.fmt/formatter "MMM 'the' do 'be with you'")
   (time/plus (time/local-date 2018 1 4)
              (time/period :months 4))))

(defexample goog-sha-example
  (comment
   (import 'goog.crypt.Sha256)
   (require '[goog.crypt :as crypt]))
  (let [digest (Sha256.)]
    (.update digest "Hello ClojureScript")
    (crypt/byteArrayToHex (.digest digest))))

(defexample goog-numfmt-example
  (comment
   (import 'goog.i18n.NumberFormat)
   (require '[goog.i18n.NumberFormat.Format :as num.formats]))
  (let [f (NumberFormat. num.formats/DECIMAL)]
    (-> (NumberFormat. num.formats/DECIMAL)
        (.setMaximumFractionDigits 3)
        (.format 1234.5678))))

(defexample goog-uri-example
  (comment
   (import 'goog.Uri))
  (-> js/window.location.href
      Uri/parse
      .makeUnique
      .toString))

(defn main []
  [:div
   [w/card-white
    [w/group-head "ClojureScript"]
    [w/slide-section
     [w/section-head "Clojure"]
     [:p.mt1 "The pragmatic lisp we know and love. Code is still data (mostly), you still have macros (kind of), and things work mostly as you expect them to."]]
    [w/slide-section
     [w/section-prehead "compiled to"]
     [w/section-head "JavaScript"]
     [:p.mt1 "A patchwork language with many runtimes and dark corners, but with a reach unlike any other language. JavaScript runs:"]
     [:ul
      [:li "In the Browser"]
      [:li "on Unix/Windows via NodeJS"]
      [:li "on the JVM via Rhino"]
      [:li "on OS X systems via " (w/link "https://developer.apple.com/documentation/javascriptcore" "JavaScriptCore")
       " (and ClojureScript via " (w/link "http://planck-repl.org/" "Planck") ")"]
      [:li "Desktop Applications such as Photoshop"]
      [:li "Extensible Programming Environments such as JUCE or Max/MSP"]]
     [:p.mt1
      "The two most popular runtimes are the browser and node.js, and ClojureScript will target either. "
      "JavaScript is embeded in many applications (such as Photoshop) for scripting purposes, but ClojureScript isn't a great fit for those, as they often require you to use the language in ways ClojureScript won't let you. "
      "If you want to write idiomatic JavaScript in a lisp, check out "
      (w/link "https://sibilant.org/" "Sibilant")
      " by Portlander Jacob Rothstein."]]
    [w/slide-section
     [w/section-prehead "via"]
     [w/section-head "Google Closure"]
     [:p.mt1 "Used for many of Google's own webapps, Closure provides an optimizing compiler and a standard library."]
     [:p.mt1
      [:strong
       "ClojureScript leverages the "
       (w/link "https://developers.google.com/closure/library/" "Closure Library")]
      " to act as a stand-in standard library to fill many of the gaps in the JavaScript standard library. "
      "It provides:"]
     [:ul
      [:li
       "Java-like Date, DateRange, DateTime, Interval, and TimeZone classes: "
       [code/clojure-example goog-date-example]
       "though you should use the "
       (w/link "https://github.com/andrewmcveigh/cljs-time" "cljs-time")
       " wrapper which mimics clj-time instead:"
       [code/clojure-example cljs-time-example]]
      [:li
       "Crypto Primitives, so you can bundle a bitcoin miner into your app:"
       [code/clojure-example goog-sha-example]]
      [:li "Formatting, String, and i18n helpers:"
       [code/clojure-example goog-numfmt-example]]
      [:li "and browser helpers for URIs, Ajax, History, LocalStorage, UserAgent:"
       [code/clojure-example goog-uri-example]]]
     [:p.mt1
      [:strong "The ClojureScript Compiler leverages the "
       (w/link "https://developers.google.com/closure/compiler/" "Closure Compiler")]
      " to remove dead code or elide development code, rewrite and minify what's left, and warn against common problems."]]]])

(defn compilers []
  [:div
   [w/card-white
    [w/slide-head "Compiling"]
    [w/section-head "The Goal"]
    [:p.mt1 "You'll need an " [w/inline-code "index.html"]
     " page loaded from a server, that pulls in your clojurescript and any other"
     " javascript you need, along with any of your CSS and any other CSS you need."]
    [:p.mt1 "Here's the one I'm using for this presentation:"]
    [code/code-file :html "resources/public/index.html" (resource-content "public/index.html")]
    [:p.mt1 "I found it simpler to pull in the code and styles for highlight.js"
     " from its CDN rather than include it in the build. We'll get into that later."]
    [:p.mt1 "Additionally, for our development environment, we want the compiler "
     " to tell the browser about code and style changes, so it can reload them in-place,"
     " without us having to manually hit refresh in the browser."]]
   [w/card-white
    [w/section-head "Simple Setup"]
    [:p.mt1 "The simplest way to get a development environment with Clojure 1.9 is to use "
     [w/inline-code "deps.edn"] ":"]
    [code/clojure-code-file "deps.edn"
     (quote {:deps {org.clojure/clojurescript {:mvn/version "1.9.946"}
                    figwheel-sidecar {:mvn/version "0.5.14"}
                    com.google.javascript/closure-compiler {:mvn.version "v20170910"}}
             :paths ["resources"]})]
    [code/clojure-code-file "watch.clj"
     (quote (require '[figwheel-sidecar.repl-api :as ra]))
     (quote (ra/start-figwheel!
             {:build-ids ["dev"]
              :all-builds [{:id "dev"
                            :figwheel true
                            :source-paths ["src"]
                            :compiler {:main "example.core"
                                       :asset-path "out"
                                       :output-to "resources/public/bundle.js"
                                       :output-dir "resources/public/out"}}]}))
     (quote (ra/cljs-repl))]
    [:p.mt1 "Drop an " [w/inline-code "index.html"]
     " file (contents discussed next slide) in "
     [w/inline-code "resources/public/index.html"]
     "and you can run it with auto-recompiling and hot-reloading with: "
     [w/inline-code "clj watch.clj"]]]
   [w/card-white
    [w/section-head "With other build systems"]
    [:p.mt4 "Integrating with " [w/link "http://boot-clj.com/" "Boot"]
     " is pretty straightforward:"]
    [code/clojure-code-file "boot example"
     (quote
       (set-env!
         :dependencies '[...
                         [org.clojure/clojurescript "1.9..."]
                         [adzerk/boot-cljs ...]
                         [adzerk/boot-cljs-repl ...]
                         [adzerk/boot-reload ...]]))
     (quote
       (require '[adzerk.boot-cljs :refer [cljs]]
                '[adzerk.boot-cljs-repl :refer [cljs-repl]]
                '[adzerk.boot-reload :refer [reload]]))
     (quote
       (deftask dev
         (comp (watch)
               (cljs-repl)
               (reload :ids :main :on-jsload ...)
               (cljs :ids :main :source-map true))))]
    [:p.mt1 "This can easily live alongside a server project, and it replaces figwheel."
     " Build ids are configured via an .edn file in your src directory."]
    [:p.mt4 "Leiningen is still more popular, but getting it to work with"
     " lein-cljsbuild and figwheel is a bit more involved:"]
    [code/clojure-code-file "lein example"
     :plugins (quote [[lein-cljsbuild "..." :exclusions [org.clojure/clojure]]
                      [lein-figwheel "..."]])
     :figwheel {:css-dirs ["resources/public/css"]
                :nrepl-port 12345}
     :cljsbuild (quote {:builds
                        {:main {:source-paths ["src"]
                                :compiler {:main ...}
                                :output-to "resources/public/js/my.js"
                                :output-dir "resources/public/js/out"
                                :asset-path "js/out"}
                         :figwheel {:on-jsload "..."}}})
     :clean-targets (quote (comment "^{:protect false}"))
     [:target-path "resources/public/js/out" "resources/public/js/main.js"]]]
   [w/card-white
    [:p.mt1 "Beyond the scope of this talk are some other clourescript compilers:"]
    [:ul
     [:li.mt1 [w/link "https://github.com/anmonteiro/lumo" "Lumo"]
      ", a standalone ClojureScript environment based on nodejs and V8, still"
      " considered somewhat experimental"]
     [:li.mt1 "and " [w/link "https://github.com/mfikes/planck" "Planck"]
      ", a standalone ClojureScript repl for JavaScript Core"]]]])

(defn hot-reloading-repl []
  [:div
   [w/card-white
    [w/slide-head "Hot Reloading and the Repl"]
    [:p "<demo> hot reloading, repl, devtools"]]])

(defexample basic-interop
  (let [d (js/Date.)]
    (.setFullYear d 2015)
    (.toString d)))

(defexample conversion
  (let [v (clj->js {:foo "bar"})]
    {:js (js/JSON.stringify v)
     :cljs (js->clj v)}))

(defexample property-access
  (let [v #js {:foo "bar"}]
    (.-foo v)))

(defn main-differences []
  [:div
   [w/card-white
    [w/slide-head "Main differences from Clojure"]
    [:p.mt3 "This is still clojure, but not " [:em "quite"] " as you know it:"]
    [w/section-head "Concurrency"]
    [:ul
     [:li.mt1 "No agents, refs, or STM. Just atoms."]
     [:li.mt1 "vars are not reified at runtime"]
     [:li.mt1 [w/inline-code "def"] " produces regular js variables"]
     [:li.mt1 "no built-in futures"]]
    [w/section-head "Macros"]
    [:ul
     [:li.mt1 "Macros are evaluated at the compilation stage, in the JVM"
      " and do not have access to a JS runtime unless you bring one."
      " The generated code must, however, target the JS runtime and not the JVM."]
     [:li.mt1 "Macros must be required in the namespace with"
      [w/inline-code ":require-macros"]
      " or with sugar:"
      [w/inline-code ":require [my-ns.core :include-macros true]"]]
     [:li.mt1 "Macros can share names with functions. For example, "
      [w/inline-code "cljs.core/+"] " is both a macro: "
      [w/inline-code "(+ 1 2 3)"] " and a function: "
      [w/inline-code "(reduce + [1 2 3])"]]]
    [w/section-head "Namespaces"]
    [:ul
     [:li.mt1 "must use " [w/inline-code ":only"] " form of " [w/inline-code ":use"]]
     [:li.mt1 [w/inline-code "require"] " does not support " [w/inline-code ":refer :all"]]
     [:li.mt1 [w/inline-code ":import"] " only supports Closure classes"]
     [:li.mt1 "Some " [w/inline-code "clojure/"]
      " namespaces are " [w/inline-code "cljs/"]]]
    [w/section-head "Interop"]
    [:ul
     [:li.mt1 "javascript global namespace available at js/"]
     [:li.mt1 "works mostly as expected:"
      [code/clojure-example basic-interop]]
     [:li.mt1 [w/inline-code "#js"] " reader macro for creating raw javascript"
      " data literals"]
     [:li.mt1 [w/inline-code "js->clj"] " and " [w/inline-code "clj->js"]
      " functions to convert data back and forth. Mostly sane."
      [code/clojure-example conversion]]
     [:li.mt1 "properties are accessed specifically:"
      [code/clojure-example property-access]]]]])

(defn js-libs []
  [:div
   [w/card-white
    [w/slide-head "Tapping into JavaScript's Libraries"]
    [:p.mt3 [w/link "http://cljsjs.github.io/" "cljsjs"] " offers up a lot of"
     " community javascript libraries pre-optimized for the closure compiler"
     " made available as jar files. You can add them to your project with"
     " a dependency vector:"]
    [code/clojure-code '[cljsjs/jquery "3.2.1-0"]]
    [:p.mt2 "Which you could then use like:"]
    [code/clojure-code
     (quote (ns my-ns (:require [cljsjs/jquery])))
     (quote (.. (js/$ "#some-ele") show (addClass "foo")))]
    [:p.mt4 "Recently an " [w/inline-code :npm-deps] " map option was added"
     " to the clojurescript compiler which allows it to pull libraries straight"
     " from what's become the motherlde of javascript, "
     [w/link "https://npmjs.org" "npm"] ". I wasn't able to get this to work"
     " in this project, but you should be able to add this to your compiler"
     " options:"]
    [code/clojure-code {:npm-deps {:react "16.0.0"}}]
    [:p.mt1 "and then use it like:"]
    [code/clojure-code
     (quote (ns my-ns (:require [react :refer [createElement]]
                                [react-dom])))
     (quote (react-dom/render (createElement "div" nil "hello")
                              (js/getElementById "app")))]
    [:p.mt1 "And the closure compiler is able to do all its tree-shaking and"
     " dead code elimination magic. It's a big deal because it not only allows"
     " you to tap into a massive ecosystem, but makes it easier to use your"
     " own JavaScript alongside your ClojureScript."]]])



(defn cljc []
  [:div
   [w/card-white
    [w/slide-head "cljc and shared code"]
    [code/pretty-code :clojure
     "(defn split-impl [arg] #?(:cljs (js/some-thing arg
                                :clj (SomeThing. arg))))"]
    [:p ""]]])
