(ns diplomaform.handler
  (:require
   [reitit.ring :as reitit-ring]
   [diplomaform.middleware :refer [middleware]]  
   [clojure.data.json :as json]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))


(def mount-target
  [:div#app
   [:h2 "Welcome to diplomaform"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "diplomaform.core.init_BANG_()"]]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn json-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (slurp "resources/qa.json")})


;; Define atom with answers state
;; {"title": "Example Survey",
;;  "answers": [{"question": "Enter your city:",
;;                    "type": "free-text",
;;                    "required": false},
;;                   {"question": "Do you like this survey?",
;;                    "required": true,
;;                    "type": "single-choice",
;;                    "values": [{"Yes": 4}, {"No": 5}]},
;;                   {"question": "What are your favorite fruits?",
;;                    "type": "multiple-choice",
;;                    "required": true,
;;                    "values": [{"Apple": 4}, {"Orange": 2}, {"Banana": 4}, {"Tomato": 3}]},
;;                    {"question": "Do you like this survey?",
;;                     "required": true,
;;                     "type": "single-choice",
;;                     "values": [{"Yes": 4}, {"No": 5}]}
                  ;;  ]}

(def answers (atom {}))

(defn handler-res
  [_request]
    {:status 200
     :headers {"Content-Type" "application/json"}
     ;; Return  answers from answers state
     ;;atom
     :body (str _request)
    ;;  (json/write-str @answers)
     })


(defn update-results
  [_request]
  ;; Log data you get from client
  (clojure.pprint/pprint _request)
  (let [data (:body _request)]
    (swap! answers data)
    (clojure.pprint/pprint @answers)
    ;; extract data from request
    ;; Iterate over answers and update state in answers atom
    )
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "Updated"})

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/stats" {:get {:handler index-handler}}]
     ["/qa" {:get {:handler json-handler}}]
     ["/res" {:get {:handler handler-res}}]
     ["/answers" {:post {:handler update-results}}]
     ]
    )
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
