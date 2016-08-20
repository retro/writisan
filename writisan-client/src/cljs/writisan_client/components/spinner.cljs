(ns writisan-client.components.spinner
  (:require [reagent.core :as reagent]))

(defn render-line [opacities color i]
  [:rect {:x 46.5 :y 40 :width 7 :height 20 :rx 5 :ry 5 :fill color
          :style {:opacity (get opacities i)}
          :transform (str "rotate(" (* i 30) " 50 50) translate(0 -30)")}])

(defn generate-opacities [current]
  (let [opacities (into [] (map (fn [i]
                                  (* 0.083333333 (- 12 i))) (range 0 12)))
        [left right] (split-at (- 12 current) opacities)]
    (into [] (concat right left))))

(defn next-idx [idx]
  (if (= idx 11)
    0
    (+ idx 1)))

(defn spinner [dim color]
  (let [current-idx (reagent/atom 0)]
    (fn []
      (let [opacities (generate-opacities @current-idx)]
        (js/setTimeout #(reset! current-idx (next-idx @current-idx)) 40)
        (into [:svg.spin {:width (str dim "px") :height (str dim "px") :view-box "0 0 100 100"
                          :preserve-aspect-ratio "xMidYMid"}]
              (concat [[:rect {:x 0 :y 0 :width 100 :height 100 :fill "none"}]]
                      (map (partial render-line opacities color) (range 0 12))))))))
