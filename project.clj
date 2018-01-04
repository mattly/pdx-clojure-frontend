(defproject pdx-clojure-constructing-interfaces "0.0.1"
  :description "Explain front-end development with re-frame to non-frontend clojurists"
  :min-lein-version "2.8.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 [reagent "0.8.0-alpha2"] ;; includes a recent stable version of react
                 [re-frame "0.10.2"]

                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [cljsjs/react-color "2.13.1-0"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]
            [lein-figwheel "0.5.14"]]

  :cljsbuild
  ;; `lein figwheel` doesn't take profiles into account, so the main options
  ;; become our dev options, and we toggle these out for production builds
  {:builds
   {:main
    {;; where are the clojurescript and cljx files?
     :source-paths ["src" "env/dev"]

     ;; compiler options: https://clojurescript.org/reference/compiler-options
     :compiler
     {:main coninter.core
      ;; where are we compiling to?
      :output-to "resources/public/js/main.js"
      ;; where should intermediate files go?
      :output-dir "resources/public/js/out"
      ;; where should the non-optimized script load intermediate files from?
      :asset-path "js/out"

      ;; handles loading development-time side-effects
      :preloads [devtools.preload]

      ;; will load modules from Node Problem Manager
      :npm-deps {}
      :install-deps true}

     ;; injects figwheel loader into JS build
     :figwheel {:on-jsload "coninter.core/reload"}}}}

  :figwheel {:css-dirs ["resources/public/css"]
             :open-file-command "atom" ;; sadly this can't be set per-user
             :nrepl-port 7888}

  :profiles
  {:dev
   {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                   [com.cemerick/piggieback "0.2.1"]
                   [proto-repl "0.3.1"]
                   [binaryage/devtools "0.9.8"]]}}

  :clean-targets ^{:protect false} [:target-path
                                    "resources/public/js/out"
                                    "resources/public/js/main.js"])
