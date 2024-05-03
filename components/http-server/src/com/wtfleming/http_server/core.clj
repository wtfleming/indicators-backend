(ns com.wtfleming.http-server.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

;; Generic Ring http server component that will start an
;; embedded Jetty web server.
(defrecord HttpServer [port
                       handler-fn
                       http-server]
  component/Lifecycle
  (start [this]
    (println "STARTING HttpServer component on port" port)

    (assoc this :http-server (run-jetty handler-fn {:port port
                                                    :join? false})))

  (stop [this]
    (println "STOPPING HttpServer component")
    this))

(defn create [handler-fn port]
  (component/using (map->HttpServer {:handler-fn handler-fn
                                     :port port})
                   [:db]))
