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
    (str "<h1>TODO indicators type " type "!</h1>")
    "<h1>TODO get all indicators</h1>"))

(defroutes app-routes
  (GET "/indicators" [] get-indicators-handler)
  (GET "/indicators/:id" [id] (str "<h1>TODO get indicator with id " id "!</h1>"))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      kp/wrap-keyword-params
      params-middleware/wrap-params))

(defn start! [port]
  (reset! server (run-jetty app {:port port
                                 :join? false})))

(defn -main [& args]
  #_(println (indicator/hello (first args)))

  (start! 8080))

(comment
  ;; FIXME make this work - probably belongs somewhere else
  ;; see https://cljdoc.org/d/polylith/clj-poly/0.2.19/doc/development
  (start! 8080)

  ;;
  )
