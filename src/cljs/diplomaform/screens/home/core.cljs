(ns diplomaform.screens.home.core
  (:require
   [re-frame.core :as re-frame]
   [diplomaform.events :as events]
   [diplomaform.subs :as subs]))

;-----------------
; question 

(def counter (atom 0))

(def id-atom (atom {}))

(defn text [counter]
  (let [id (str "input-" counter)]
    [:div {:class "form-group"}
     [:input.input
      {:type "text"
       :class "form-control"
       :id id
       :name id}]]))

(defn radio [counter props]
  (for [i (:values props)
        :let [id (str i counter)]]
    [:div {:class "form-check"}
     [:input
      {:id id
       :type "radio"
       :class "form-check-input choise"
       :name (str (:question props))}]
     [:label {:class "form-check-label"}
      (str i)]
     [:br]]))


(defn checkbox [counter props]
  (for [i (:values props)
        :let [id (str i counter)]]
    [:div {:class "form-check"}
     [:input
      {:id id
       :type "checkbox"
       :class "form-check-input choise"}]
     [:label {:class "form-check-label"} (str i)]
     [:br]]))

(defn question [props counter]
  [:div {:class "container"}
   [:h5 (str counter ". " (:question props) (when (= (:required props) true) " *"))]
   [:form
    (when (= (:type props) "free-text") (text counter))
    (when (= (:type props) (str "single-choice")) (radio counter props))
    (when (= (:type props) (str "multiple-choice")) (checkbox counter props))]])


;-----------------
; questions form 


(defn extract-text [counter]
  (.-value  (.getElementById js/document (str "input-" counter))))

(defn extract-single-choice [props counter]
  (for [i (:values props)
        :let [id (str i counter)]]
    {i (.-checked (.getElementById js/document id))})
  )

(defn extract-multiple-choice [props counter]
  (for [i (:values props)
        :let [id (str i counter)]]
    {i (.-checked (.getElementById js/document id))}))

;; {:question "Do you like this survey?"
;;  :required true
;;  :type "single-choice"
;;  :values ["Yes" "No"]}
(defn extract-value [props counter]
  (assoc props :values
         (case (:type props)
           "free-text" (extract-text counter)
           "single-choice" (extract-single-choice props counter)
           "multiple-choice" (extract-multiple-choice props counter))))

;; {:question "Do you like this survey?"
;;  :required true
;;  :type "single-choice"
;;  :values [{"Yes": true} {"No": false}]}

(defn on-submit [event]
  (.preventDefault event)
  (let [data (re-frame/subscribe [::subs/data])
        results (map-indexed (fn [counter props]
                               (extract-value props counter))
                             (:questions @data))]
    (re-frame/dispatch-sync [::events/post-results results])
    ;; Update data with results (assoc into :answers key)
    ;; Send that data via re-frame to server 
    (cljs.pprint/pprint @data)
    (cljs.pprint/pprint results)))




(defn home-page []
  (let [data (re-frame/subscribe [::subs/data])
        smth (re-frame/dispatch [::events/something])]
    (fn []
      [:form
       {:on-submit #(on-submit %)}
       [:div {:class "container field"}
        [:div
         [:div {:class "d-flex justify-content-center"}
          [:h3 "Answer, please :"]]

         (for [i (:questions @data)]
           [question i (dec (swap! counter inc))])

         [:button.button {:color "btn btn-light button"
                          :type "submit"}
          "Submit"]]]])))






