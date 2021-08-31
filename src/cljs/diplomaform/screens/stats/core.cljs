(ns diplomaform.screens.stats.core
  (:require
   [reagent.core :as r]
   [diplomaform.screens.reused.diagrampart :refer [diagram-part]]
   [re-frame.core :as re-frame]
   [diplomaform.events :as events]
   [diplomaform.subs :as subs]))

(def max-count
  (r/atom 10))

(defn diagram []
  (let [answers (re-frame/subscribe [::subs/answers])
        smth (re-frame/dispatch [::events/something-res])]
    [:div {:class "container field"}
     [:div
      [:div {:class "d-flex justify-content-center"}
       [:h3 "Results diagram:"]]
      (prn (:answers @answers))
      (for [i (:answers @answers)]
        [diagram-part i @max-count])]]))
