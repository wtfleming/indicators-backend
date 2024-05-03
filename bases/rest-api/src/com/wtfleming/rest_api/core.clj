(ns com.wtfleming.rest-api.core
  (:require [com.stuartsierra.component :as component]
            [com.wtfleming.http-server.interface :as http-server]
            [com.wtfleming.indicator.interface :as indicator]
            [com.wtfleming.in-memory-database.interface :as db]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.params :as params-middleware]
            [ring.util.response :refer [response]])
  (:gen-class))

;; -------------------------
;; HTTP Server handers
;; -------------------------
(defn get-indicators-handler [req]
  (let [db (:db req)
        type (-> req :params :type)]
    (if type
      (response (indicator/get-all-indicators-by-type db type))
      (response (indicator/get-all-indicators db)))))

(defn get-indicator-by-id-handler [req]
  (let [id (-> req :params :id)
        id (Integer/parseInt id)
        db (:db req)]
    ;; TODO if result is nil, return a 404 instead?
    (response (indicator/get-indicator-by-id db id))))

(defroutes app-routes
  (GET "/indicators" [] get-indicators-handler)
  (GET "/indicators/:id" [] get-indicator-by-id-handler)
  (route/not-found "<h1>Page not found</h1>")) ;; probably should return something else and have it be JSON?

;; We want to store the database component in a request
;; so that we can query it.
;; There is probably a better way to do this than storing
;; the system in an atom and getting it from middleware like this.
(defonce ^:private the-system (atom nil))

(defn add-db-to-req-middleware [handler]
  (fn [req]
    (handler (assoc req :db (:db @the-system)))))

(def app
  (-> app-routes
      wrap-json-response
      add-db-to-req-middleware
      kp/wrap-keyword-params
      params-middleware/wrap-params))

;; -------------------------

(defn new-system [port]
  (component/system-map :db (db/create "indicators.json")
                        :http-server (http-server/create app port)))

(defn -main [& [port]]
  (let [port (or port (get (System/getenv) "PORT" 8080))
        port (if (int? port) port (Integer/parseInt port))
        system (component/start (new-system port))]
    (reset! the-system system))

  ;; Block forever so the server does not shutdown.
  ;; If we needed to cleanly shutdown, consider using something like
  ;; https://commons.apache.org/proper/commons-daemon/
  ;; But for the purposes of this exercise we'll just stop the server
  ;; with something like a SIGINT
  (deref (promise)))
