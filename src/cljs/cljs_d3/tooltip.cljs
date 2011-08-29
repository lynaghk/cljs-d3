(ns cljs-d3.tooltip
  (:require [goog.dom :as dom]
            [goog.style :as style]))

(def tooltip-id "cljs-d3-tooltip")

(defn init! []
  "Add tooltip <div> to the page."
  (if (not (dom/getElement tooltip-id))
    (let [tt (dom/createDom "div" (.strobj {"id" tooltip-id
                                            "style" "position: absolute;"}))]
      (dom/appendChild (.body js/document) tt)
      tt)))

(defn show!
  "Show tooltip on the page at offset `left, top` with innerHTML `html`.
     margin: vector pair of pixel offsets from x/y to show tooltip"
  [left top html & {:keys [margin]
                    :or {margin [0 0]}}]
  
  (let [tt (dom/getElement tooltip-id)]
    (set! (.innerHTML tt) html)
    (style/setPosition tt (+ left (margin 0)) (+ top (margin 1)))
    (style/showElement tt true)))

(defn hide! []
  "Hide the tooltip"
  (style/showElement (dom/getElement tooltip-id) false))
