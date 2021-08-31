(ns diplomaform.subs
  (:require
 [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::data
 (fn [db]
   (get db :data)))

(re-frame/reg-sub
 ::answers
 (fn [db]
   (get db :answers)))
