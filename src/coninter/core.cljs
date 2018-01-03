(ns coninter.core
  (:require
   [coninter.slides :as slides]
   [reagent.core :as reagent]))

(defn mount []
  (reagent/render [slides/presentation] (js/document.getElementById "app")))

(defn initialize []
  (js/console.log :initializing!)
  (mount))

(defn reload []
  (js/console.log :reloading!)
  (mount))

;; I haven't yet found a satisfying equivalent to `-main`, so this is my hacky workaround
(defonce *loaded? (atom false))
(when-not @*loaded?
  (initialize)
  (reset! *loaded? true))
