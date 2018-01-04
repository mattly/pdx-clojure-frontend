(ns coninter.components.widgets
  (:require-macros
    [coninter.components.code :refer [defn-with-src]])

  (:require
   [clojure.string :as str]))

(defn ele
  "Construct a keyword with some classes for a 'preset' element."
  [tag classes]
  (->> classes (map name) (str/join ".") (apply str (name tag) ".") keyword))

(def card-base #{:mv4 :pa3 :br3})
(def card-white (ele :div (conj card-base :bg-white)))

(def group-head (ele :h2 #{:mt2 :mb0 :f2 :lh-solid}))
(def slide-head (ele :h3 #{:mt2 :mb2 :f3 :lh-solid}))
(def slide-section (ele :div #{:mv3}))
(def section-head (ele :h4 #{:f4 :mv2}))
(def section-prehead (ele :p #{:f6 :mb1 :near-black}))

(defn-with-src link [href text]
  [:a.dark-red {:href href} text])

(defn self-link [l] (link l l))

(defn inline-code [c]
  [:code.dark-blue.f6 c])

(def td (ele :td #{:bb :b--light-gray :p1}))
