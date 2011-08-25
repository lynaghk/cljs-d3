(ns cljs-d3.scales
  (:require [cljs-d3.core :as d3])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn linear
  [& {:keys [domain range nice]
      :or {domain     [0 1]
           range      [0 1]
           rangeRound nil
           nice       false
           clamp      false}}]

  (-> (.. js/d3 scale (linear))
      (.domain (d3/jsArr domain))
      (.range (d3/jsArr range))
      ((d3m/shim-if rangeRound (d3/jsArr rangeRound)))
      ((d3m/shim-if nice))
      ((d3m/shim-if clamp))))
