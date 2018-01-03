(ns coninter.components.navigation
  (:require-macros
   [coninter.components.code :refer [defsrc]])
  (:require
   [re-frame.core :as rf]))

(defsrc location-hash-src
  (rf/reg-cofx
   :browser/location-hash
   (fn [cofx _]
     (assoc cofx :browser/location.hash
            js/window.location.hash))))
