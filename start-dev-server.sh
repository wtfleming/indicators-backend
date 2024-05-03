#!/bin/sh

set -e

clojure -M:dev -m com.wtfleming.rest-api.core
