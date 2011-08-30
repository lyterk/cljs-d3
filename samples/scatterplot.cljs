(ns sample.main
  (:require [cljs-d3.core :as d3] ;;core must be imported as 'd3' for the cljs-d3 threading macro to work.
            [cljs-d3.scale :as scale]
            [cljs-d3.tooltip :as tooltip])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn rand [] ((.random js/Math)))

(let [Width       300 ;;Width in pixels
      n           100 ;;Number of data
      scale       (scale/linear :domain [0 1] :range [0 Width])

      sample-data (for [_ (range n)]
                    {:x (rand)
                     :y (rand)
                     :class (if (> (rand) 0.5)
                              "A" "B")})

      scatterplot (d3m/-> d3/d3 (select "#example")
                          (append "svg:svg")
                          (style {:border "2px solid darkGray"
                                  :border-radius 8})
                          (attr {:width  Width
                                 :height Width}))

      points      (d3m/-> scatterplot
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
  (d3/on points "mousemove"
         #(let [e (d3/event)]
            (tooltip/show! (.pageX e) (.pageY e)
                           (str "<div style=\"background-color: white; border: 1px solid black;\">"
                                                   "Datum: (" (:x %) ", " (:y %) ")"
                                                   "</div>"))))
  (d3/on points "mouseout"
         #(tooltip/hide!)))
