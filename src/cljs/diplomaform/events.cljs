(ns diplomaform.events
   (:require
    [re-frame.core :as re-frame]
    [diplomaform.db :as db]
    [ajax.core :as ajax]
    [day8.re-frame.http-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::something
 (fn [{db :db} _] 
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3000/qa"
                 :timeout         8000
                 :format (ajax/json-response-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::generate-db]
                 :on-failure      [:bad-http-result]}}))   

(re-frame/reg-event-fx
 ::something-res
 (fn [{db :db} _]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3000/res"
                 :timeout         8000
                 :format          (ajax/json-response-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::generate-db-res]
                 :on-failure      [:bad-http-result]}}))


(re-frame/reg-event-fx
 ::post-results
 (fn [_world [_ val]]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:3000/answers"
                 :params          val
                 :timeout         5000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::generate-answers]
                 :on-failure [:event-id]}}))

(re-frame/reg-event-db
 ::generate-answers
 (fn [db [_ response]]
  ;;  (assoc db :data
          (js->clj response)))
;; )

(re-frame/reg-event-db
 ::generate-db
 (fn [db [_ response]]
   (assoc db :data
          (js->clj response))))

(re-frame/reg-event-fx
 :bad-http-result
 (fn [db [_ response]]
   (prn response)
   ))

(re-frame/reg-event-db
 ::generate-db-res
 (fn [db [_ response]]
   (assoc db :answers
          (js->clj response))))