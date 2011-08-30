(ns cljs-d3.scale
  (:require [cljs-d3.core :as d3])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn linear
  [& {:keys [domain range nice range-round clamp]}]
  (-> (.. js/d3 scale (linear))
      (.domain (d3/jsArr domain))
      (.range (d3/jsArr range))
      ((d3m/shim-if-arg rangeRound) (d3/jsArr range-round nil))
      ((d3m/shim-if-arg clamp) clamp)
      ((d3m/shim-if nice) nice)))
