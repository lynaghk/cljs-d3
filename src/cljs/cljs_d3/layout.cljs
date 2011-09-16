(ns cljs-d3.layout
  (:require [cljs-d3.core :as d3])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn histogram
  [& {:keys [value range bins frequency]}]
  (let [h (-> (.. d3/d3 layout (histogram))
              ((d3m/shim-if-arg value) value)
              ((d3m/shim-if-arg range) (d3/jsArr range nil))
              ((d3m/shim-if-arg bins) (if (number? bins)
                                        bins
                                        (d3/jsArr bins nil)))
              ((d3m/shim-if-arg frequency) frequency))]
    
    ;;Return a wrapped histogram that coerces incoming data correctly.
    (fn [x]
      (condp = x
          :value (. h (value))
          :range (. h (range))
          :bins (. h (bins))
          :frequency (. h (frequency))
          (h (d3/jsArr x))))))
