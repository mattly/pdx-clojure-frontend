(ns coninter.slides.cljs
  (:require-macros
   [coninter.components.code :refer [defexample resource-content]])
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
    [w/slide-head "Compiling and Hot-Reloading"]
    [:h3.f3 "The Goal"]
    [:p.mt1 "You'll need an " [w/inline-code "index.html"]
     " page loaded from a server, that pulls in your clojurescript and any other"
     " javascript you need, along with any of your CSS and any other CSS you need."]
    [:p.mt1 "Here's the one I'm using for this presentation:"]
    [code/code-file :html "resources/public/index.html" (resource-content "public/index.html")]
    [:p.mt1 "I found it simpler to pull in the code and styles for highlight.js"
     " from its CDN rather than include it in the build. We'll get into that later."]
    [:p.mt1 "Additionally, for our development environment, we want the compiler "
     " to tell the browser about code and style changes, so it can reload them in-place,"
     " without us having to manually hit refresh in the browser."]

    [:h3.f3 "Simple Setup"]
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
     [w/inline-code "clj watch.clj"]]]])
