(ns com.wtfleming.rest-api.core
  (:require [com.stuartsierra.component :as component]
            [com.wtfleming.http-server.interface :as http-server]
            [com.wtfleming.indicator.interface :as indicator]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.params :as params-middleware])
  (:gen-class))

;; -------------------------
;; HTTP Server handers
;; -------------------------
(defn get-indicators-handler [req]
  (let            [db nil] ;; FIXME actually get the db from the req when it has been implemented
    (if-let [type (-> req :params :type)]
      (indicator/get-all-indicators-by-type db type)
      (indicator/get-all-indicators db))))

(defn get-indicator-by-id-handler [req]
  (let [id (-> req :params :id)
        db nil] ;; FIXME actually get the db from the req when it has been implemented
    (indicator/get-indicator-by-id db id)))

(defroutes app-routes
  (GET "/indicators" [] get-indicators-handler)
  (GET "/indicators/:id" [] get-indicator-by-id-handler)
  (route/not-found "<h1>Page not found</h1>")) ;; TODO return something else, JSON?

(def app
  (-> app-routes
      kp/wrap-keyword-params
      params-middleware/wrap-params))

;; -------------------------

(defn new-system []
  (component/system-map :http-server (http-server/create app 8080)))

(defn -main [& _args]
  ;; TODO should be able to specify the port
  (component/start (new-system))

  ;; Block forever so the server does not shutdown.
  ;; If we needed to cleanly shutdown, consider using something like
  ;; https://commons.apache.org/proper/commons-daemon/
  ;; But for the purposes of this exercise we'll just stop the server
  ;; with something like a SIGINT
  (deref (promise)))

(comment
  ;; FIXME make this work in a REPL - probably belongs somewhere else
  ;; see https://cljdoc.org/d/polylith/clj-poly/0.2.19/doc/development
  ;;(start! 8080)

  ;;
  )
