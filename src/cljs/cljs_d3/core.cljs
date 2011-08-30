(ns cljs-d3.core
  (:require-macros [cljs-d3.macros :as d3m]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Some nice JavaScript helpers

(defn kstr
  "Stringify keywords"
  [k] (if (keyword? k) (name k) k))

(defn pxstr
  "Converts a number to a string with suffix 'px'"
  [v] (if (number? v) (str v "px") v))

(defn jsArr
  "Recursively converts a sequential object into a JavaScript array"
  [seq]
  (.array (vec (map #(if (sequential? %) (jsArr %) %)
                    seq))))

(defn jsObj
  "Convert a clojure map into a JavaScript object"
  [obj]
  (into {} (map (fn [[k v]]
                  (let [k (if (keyword? k) (name k) k)
                        v (if (keyword? v) (name v) v)]
                    (if (map? v)
                      [k (jsObj v)]
                      [k v])))
                obj)))

(defn p [x]
  (.log js/console (cond
                    (sequential? x) (jsArr x)
                    (map? x) (jsObj x)
                    :else x))
  x)



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;D3 shims
(def d3 js/d3)
(def event #(.event js/d3))

(d3m/shim select)
(d3m/shim selectAll)
(d3m/shim classed)
(d3m/shim property)
(d3m/shim text)
(d3m/shim html)

(d3m/shim append)
(d3m/shim insert)
(d3m/shim remove)

(d3m/shim on)

(d3m/shim enter)
(d3m/shim exit)



;;TODO: Replace attr & style with macros so map arguments expand at compile time
(defn attr
  ([sel x]
     (if (map? x)
       (doseq [[k v] x] (.attr sel (kstr k) v))
       (.attr sel x))
     sel)
  ([sel k v] (.attr sel (kstr k) v)))

(defn style
  ([sel x]
     (if (map? x)
       (doseq [[k v] x] (.style sel (kstr k) (pxstr v)))
       (.style sel (pxstr x)))
     sel)
  ([sel k v] (.style sel (kstr k) (pxstr v))))

(defn data [sel x]
  (.data sel (if (sequential? x)
               (jsArr x)
               #(jsArr (apply x %&)))))


(defn transition [sel & {:keys [duration delay]
                         :or {duration 250
                              delay      0}}]
  (-> (. sel (transition))
      (.duration duration)
      (.delay delay)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;D3/DOM helpers

(defn make-last-child
  "Moves an element to the end of its parent, useful for changing SVG z-index"
  [el]
  (-> el
      (.parentNode)
      (.appendChild el)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Data-ish helpers

(defn min-max
  "Returns the vector [min-val max-val] for a data vector.
   If a :dimension function is passed in, it is mapped over the data before calculating min/max."
  [data & {:keys [dimension]}]
  (let [vals (if dimension
               (map dimension data)
               data)]
    [(apply min vals)
     (apply max vals)]))

(defn min-0-max
  "Similar to (min-max), but returning [min-val 0 max-val]."
  [data & {:keys [dimension]}]
  (let [mm (min-max data :dimension dimension)]
    [(nth mm 0) 0 (nth mm 1)]))

(defn avg [data & {:keys [dimension]}]
  (let [vals (if dimension
               (map dimension data)
               data)]
    (/ (apply + vals)
       (count vals))))

(defn quantile
  "Returns the quantiles of a dataset.
     probs: ntiles of the data to return, defaults to [0 0.25 0.5 0.75 1]
     dimension: a function to map over the data before calculating quantiles
  Algorithm transcribed from Jason Davies; https://github.com/jasondavies/science.js/blob/master/src/stats/quantiles.js"
  [data & {:keys [dimension probs]
           :or {dimension identity
                probs [0 0.25 0.5 0.75 1]}}]
  (let [vals (into [] (sort (map dimension data)))
        n-1 (dec (count vals))]
    (map #(let [index (+ 1 (* % n-1))
                lo    (.floor js/Math index)
                h     (- index lo)
                a     (vals (dec lo))]
            (if (= h 0)
              a
              (+ a (* h (- (vals lo) a)))))
         probs)))
