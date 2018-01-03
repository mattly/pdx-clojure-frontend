(ns coninter.components.code
  (:require-macros
   [coninter.components.code :refer [defn-with-src]])
  (:require
   [cljs.pprint :refer [pprint]]
   [reagent.core :as reagent]
   [clojure.string :as str]))

(defn-with-src pretty-code [code-type code]
  (let [*code-ele (atom nil)
        set-code-ele! (partial reset! *code-ele)
        render-code #(js/hljs.highlightBlock @*code-ele)]
    (reagent/create-class
     {:component-did-mount render-code
      :component-did-update render-code
      :reagent-render
      (fn [code-type code]
        [:pre.ma0
         [:code.pa0.f6 {:class code-type :ref set-code-ele!} code]])})))

(defn clojure-example [{:keys [code thunk]}]
  [:div.mv3.bt.bb.b--light-gray
   [:div.bb.b--light-gray [pretty-code :clojure code]]
   [pretty-code :clojure (with-out-str (pprint (thunk)))]])

(defn code-file [code-type filename code]
  [:div.mv3.bt.bb.b--light-gray
   [:p.mv0.pv2.pl2.bb.b--light-gray.bg-near-white.fw6 filename]
   [pretty-code code-type code]])

(defn print-code [quoted-code]
  (->> quoted-code
       (map #(with-out-str (pprint %)))
       (str/join "\n")))

(defn clojure-code-file [filename & quoted-code]
  [code-file :clojure filename (print-code quoted-code)])

(defn clojure-code [& quoted-code]
  [pretty-code :clojure (print-code quoted-code)])

(defn hiccup-example [{:as example :keys [code thunk]}]
  [:div.mv3.bt.bb.b--light-gray
   [pretty-code :clojure code]
   [:div.pv2.bt.b--light-gray (thunk)]])
