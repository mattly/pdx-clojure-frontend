(ns coninter.components.code
  (:require
   [clojure.java.io :as io]
   [clojure.pprint :as pprint :refer [pprint]]
   [clojure.string :as str]))

(defn print-src [forms]
  (binding [pprint/*print-right-margin* 60]
   (->> forms
        (map #(with-out-str (pprint %)))
        (map #(str/replace % #"clojure.core/" ""))
        (str/join "\n"))))

(defn print-code [forms]
  (pprint/with-pprint-dispatch pprint/code-dispatch
    (print-src forms)))

(defmacro defexample [id & forms]
  `(def ~id
     {:code ~(print-code forms)
      :thunk (fn [] (do ~@forms))}))

(defmacro example [& forms]
  {:code (print-code forms)
   :thunk `(fn [] (do ~@forms))})

(defmacro defn-with-src [fname args & forms]
  (let [src `(defn ~fname ~args ~@forms)]
   `(do
      (def ~(symbol (str (name fname) "-src"))
        ~(print-code [src]))
      ~src)))

(defmacro defsrc [s & forms]
  `(do
     (def ~s ~(print-code forms))
     ~@forms))

(defmacro resource-content [path]
  (-> path io/resource slurp))
