(ns cljs-d3.macros)

(defmacro attr
  "Call JavaScript function, replacing Clojure map with JavaScript object"
  [d3 attr-map]
  (let [d3# d3]
    `(do
       ~@(for [[k# v#] attr-map]
           `(.attr ~d3# ~k# ~v#))
       ~d3#)))

(defmacro $ready
  "Call forms on document ready"
  [& forms]
  `(.ready (js/jQuery "document")
           #(do ~@forms)))

(defmacro debug
  "Call a function to print D3 data and indexes at this point"
  [d3]
  `(.call ~d3 (fn [d i]
                (js/p d)
                (js/p i))))

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
