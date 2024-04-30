(ns com.wtfleming.rest-api.core
  (:require [com.wtfleming.indicator.interface :as indicator])
  (:gen-class))

(defn -main [& args]
  (println (indicator/hello (first args)))
  (System/exit 0))
