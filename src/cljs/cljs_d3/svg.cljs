(ns cljs-d3.svg
  (:require [cljs-d3.core :as d3])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn axis
  [scale & {:keys [orient ticks tick-format tick-size]}]
  (-> (.. d3/d3 svg (axis))
      (.scale scale)
      ((d3m/shim-if-arg orient) orient)
      ((d3m/shim-if-arg tickSize) tick-size)
      ((d3m/shim-if-arg ticks) ticks)
      ((d3m/shim-if-arg tickFormat) tick-format)))
