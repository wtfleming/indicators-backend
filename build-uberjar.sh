#!/bin/sh

set -e

clojure -T:build uberjar :project indicators-backend
