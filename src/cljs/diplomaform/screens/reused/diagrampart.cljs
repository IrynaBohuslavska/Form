(ns diplomaform.screens.reused.diagrampart)

(defn diagram-part [props max-count]
  [:div
   [:h4 (:question props)]
   (for [i (:values props)]
     [:<>
      (prn (:values i))
      [:div [:p (str i " - " (int (Math/floor (/ (* (get i 1) 100) max-count))) "%")]]
      [:div {:style {:width (int (Math/floor (/ (* (get i 1) 900) max-count)))}
             :class "diagram-part"}]])])