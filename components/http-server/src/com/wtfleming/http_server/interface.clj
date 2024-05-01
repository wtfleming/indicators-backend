(ns com.wtfleming.http-server.interface
  (:require [com.wtfleming.http-server.core :as http-server]))

(defn create [handler-fn port]
  (http-server/create handler-fn port))
