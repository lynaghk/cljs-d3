(ns sample
  (:require [cljs-d3.core :as d3]
            [cljs-d3.scales :as scales]))

(defn rand [] ((.random js/Math)))

(let [Width       400 ;;Width in pixels
      n           100 ;;Number of data
      scale       (scales/linear :domain [0 1] :range [0 Width])
      
      sample-data (for [_ (range n)]
                    {:x (rand)
                     :y (rand)
                     :class (if (> (rand) 0.5)
                              "A" "B")})

      scatterplot (-> d3/d3 (d3/select "#example")
                      (d3/append "svg:svg")
                      (d3/style {:border "2px solid darkGray"
                                 :border-radius 8})
                      (d3/attr {:width  Width
                                :height Width}))

      points      (-> scatterplot
                      (d3/selectAll "circle.num")
                      (d3/data sample-data)
                      (d3/enter)(d3/append "svg:circle")
                      (d3/attr {:class "num"
                                :r 5
                                :fill #(condp = (:class %)
                                           "A" "darkRed"
                                           "B" "darkBlue")
                                :cx #(scale (:x %))
                                :cy #(scale (:y %))}))

      ])
