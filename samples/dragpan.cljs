(ns sample.dragpan
  (:require [cljs-d3.scale :as scale]
            [jsd3.core :as jsd3])
  (:use [cljs-d3.core :only [p d3 select selectAll append append* style attr data enter
                             on event]]
        [clojure.string :only [join]]))

(let [m 14, n 14 ;;rows and columns
      the-data (take m (partition n (repeatedly rand)))

      ;;Heatmap width/height
      width 500, height 200
      colsep 50, rowsep 50
      rwidth 48, rheight 48
      ;;Full data width/height (larger than heatmap width/height)
      dwidth (* colsep m), dheight (* rowsep n)


      ;;Current heatmap Viewbox coordinates
      vb (atom {:x 0 :y 0 :width width :height height})
      update-viewbox! (fn [x y] "Positions viewbox at x,y, subject to boundary"
                        (let [max-x (- dwidth width), max-y (- dheight height)
                              x (cond (< x 0) 0
                                      (> x max-x) max-x
                                      :else x)
                              y (cond (< y 0) 0
                                      (> y max-y) max-y
                                      :else y)]
                          (swap! vb assoc :x x :y y)))

      color (scale/linear :domain [0 0.5 1] :range ["firebrick" "white" "navy"])

      svg (-> d3 (select "body")
              (append :svg {:width width, :height height
                            :onmousemove "pan(evt)"
                            :onmousedown "pan_start(evt)"
                            :onmouseup "pan_end(evt)"}))
      svg-svg (. svg (node))

      heatmap (-> svg (append "g" {:id "heatmap"}))

      minimap (-> d3 (select "body")
                  (append :svg {:id "minimap"
                                :width 200, :height 200
                                ;;Viewbox of minimap shows all the data
                                :viewBox (join " " [0 0 dwidth dheight])}))]

  ;;Fill the heatmap with squares
  (doseq [[row i] (map vector the-data (range))]
    (-> heatmap (append "g")
        (selectAll "square")
        (data row)
        (enter) (append "svg:rect" {:x #(* colsep %2)
                                    :y (* rowsep i)
                                    :fill #(color %)
                                    :width rwidth, :height rheight})))

  ;;Make minimap
  (-> minimap
      (attr {:onmousemove "move(evt)"
             :onmouseup "release(evt)"})
      (append "use" {"xlink:href" "#heatmap"}))
  (let [minimap-svg (. minimap (node))
        map-bounds (append minimap "rect" {:id "map-bounds"
                                           :width width, :height height
                                           :x 0, :y 0
                                           :pointer-events "fill"
                                           :onmousedown "grab(evt)"})]

    (def screen-point (. minimap-svg (createSVGPoint)))
    (defn to-userspace [svg-el e]
      "Transform the mouse event coordinates from window space into SVG userspace coordinates."
      (set! (.x screen-point) (.clientX e))
      (set! (.y screen-point) (.clientY e))
      (.matrixTransform screen-point
                        (-> svg-el
                            (. (getScreenCTM))
                            (. (inverse)))))


      ;;;Minimap-moving functions
    (def click-offset (atom nil))
    (defn grab [e]
      (let [pos (to-userspace minimap-svg e)
            t (.target e)]
        (reset! click-offset [(- (.x pos) (.getAttribute t "x"))
                              (- (.y pos) (.getAttribute t "y"))])))
    (defn move [e]
      (if @click-offset
        (let [cur-point  (to-userspace minimap-svg e)
              [off-x off-y] @click-offset
              x (- (.x cur-point) off-x), y (- (.y cur-point) off-y)]
          (update-viewbox! x y))))
    (defn release [e] (reset! click-offset nil))


      ;;;Panning functions
    (def pan-offset (atom nil))
    (defn pan-start [e]
      (let [pos (to-userspace svg-svg e)]
        (reset! pan-offset [(.x pos) (.y pos)])))
    (defn pan [e]
      (if @pan-offset
        (let [cur-point  (to-userspace svg-svg e)
              [off-x off-y] @pan-offset
              [vbx vby] (map @vb [:x :y])
              x (- (.x cur-point) off-x)
              y (- (.y cur-point) off-y)]
          (update-viewbox! (- vbx x) (- vby y)))))
    (defn pan-end [e] (reset! pan-offset nil))


    ;;Bind these Clojure functions to window so the browser can find them.
    (set! (.grab js/window) grab)
    (set! (.move js/window) move)
    (set! (.release js/window) release)
    (set! (.pan js/window) pan)
    (set! (.pan_start js/window) pan-start)
    (set! (.pan_end js/window) pan-end)

    ;;Whenever the viewbox coordinates change, update the heatmap viewbox and minimap bounds box location.
    (add-watch vb :update-heatmap-viewbox
               (fn [key vb old new]
                 (attr svg {:viewBox (join " " (map new [:x :y :width :height]))})
                 (attr map-bounds (select-keys new [:x :y]))))


    ))
