(ns cljs-d3.macros)


(defmacro attr
  "Expand (attr {}) into multiple attr calls"
  [selection attr-map]
  `(do
     ~@(for [[k# v#] attr-map]
         `(.attr ~selection ~k# ~v#))
     ~selection))

(defmacro style
  "Expand (style {}) into multiple style calls"
  [selection style-map]
  `(do
     ~@(for [[k# v#] style-map]
         `(.style ~selection ~k# ~v#))
     ~selection))

(defmacro $ready
  "Call forms on document ready, if you have jQuery on the page"
  [& forms]
  `(.ready (js/jQuery "document")
           #(do ~@forms)))

(defmacro shim [name]
  "Define a proxy to native D3 method"
  `(defn ~name [sel# & args#]
     (apply (. sel# ~name) args#)))

(defmacro shim-if [name & args]
  "Inline proxy for a D3 method, if true.
   Works for argument-less mutators like `.nice()` as well as things like `.domain([0 1])`"
  (if (seq args)
    `(fn [x# test#] (if test# (. x# ~name ~@args) x#))
    `(fn [x# test#] (if test# (. x# (~name)) x#))))

(defmacro -> [base & forms]
  "Like ->, except prefixes all forms after the first with 'd3/'.
   This is a terrible hack to work around the lack of naked :use in ClojureScript"
  `(-> ~base
       ~@(for [form forms]
           (list* (symbol (str "d3/" (first form)))
                 (rest form)))))
