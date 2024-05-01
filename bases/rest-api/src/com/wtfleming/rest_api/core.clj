(ns com.wtfleming.rest-api.core
  (:require [com.wtfleming.indicator.interface :as indicator]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.params :as params-middleware])
  (:gen-class))

(def ^:private server (atom nil))

(defn get-indicators-handler [req]
  (if-let [type (-> req :params :type)]
    (indicator/get-all-indicators-by-type type)
    (indicator/get-all-indicators)))

(defn get-indicator-by-id-handler [req]
  (let [id (-> req :params :id)]
    (indicator/get-indicator-by-id id)))

(defroutes app-routes
  (GET "/indicators" [] get-indicators-handler)
  (GET "/indicators/:id" [] get-indicator-by-id-handler)
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      kp/wrap-keyword-params
      params-middleware/wrap-params))

(defn start! [port]
  (reset! server (run-jetty app {:port port
                                 :join? false})))

(defn -main [& _args]
  ;; TODO should be able to specify the port
  (println "Starting server on port 8080")
  (start! 8080))

(comment
  ;; FIXME make this work in a REPL - probably belongs somewhere else
  ;; see https://cljdoc.org/d/polylith/clj-poly/0.2.19/doc/development
  (start! 8080)

  ;;
  )
