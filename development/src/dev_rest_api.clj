(ns dev-rest-api
  (:require [com.stuartsierra.component :as component]
            [com.wtfleming.rest-api.core :as api]))


(comment

  (def system (-> 8080
                  (api/new-system)
                  (component/start)))

  (component/stop system)

  ;;
  )
