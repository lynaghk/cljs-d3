(ns sample.scatterplot
  (:require [cljs-d3.scale :as scale]
            [cljs-d3.tooltip :as tooltip]
            [jsd3.core :as jsd3])
  (:use [cljs-d3.core :only [d3 select selectAll append style attr data enter
                             on event]]))

(defn rand [] ((.random js/Math)))

(let [Width       300 ;;Width in pixels
      n           100 ;;Number of data
      scale       (scale/linear :domain [0 1] :range [0 Width])

      sample-data (for [_ (range n)]
                    {:x (rand)
                     :y (rand)
                     :class (if (> (rand) 0.5)
                              "A" "B")})

      scatterplot (-> d3 (select "#example")
                          (append "svg:svg")
                          (style {:border "2px solid darkGray"
                                  :border-radius 8})
                          (attr {:width  Width
                                 :height Width}))

      points      (-> scatterplot
                          (selectAll "circle.num")
                          (data sample-data)
                          (enter)(append "svg:circle")
                          (attr {:class "num"
                                 :r 5
                                 :fill #(condp = (:class %)
                                            "A" "darkRed"
                                            "B" "darkBlue")
                                 :cx #(scale (:x %))
                                 :cy #(scale (:y %))}))]


  ;;Add mouseover tooltip to points
  (tooltip/init!)
  (on points "mousemove"
         #(let [e (event)]
            (tooltip/show! (.pageX e) (.pageY e)
                           (str "<div style=\"background-color: white; border: 1px solid black;\">"
                                                   "Datum: (" (:x %) ", " (:y %) ")"
                                                   "</div>"))))
  (on points "mouseout"
         #(tooltip/hide!)))
