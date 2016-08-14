(defproject writisan-client "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [binaryage/devtools "0.6.1"]
                 [garden "1.3.2"]
                 [reagent "0.6.0-SNAPSHOT"]
                 [secretary "1.2.3"]
                 [funcool/cuerdas "0.7.0"]
                 [funcool/promesa "1.4.0"]
                 [cljs-ajax "0.5.3"]
                 [cljsjs/moment "2.10.6-3"]
                 [cljsjs/codemirror "5.11.0-1"]
                 [cljsjs/markdown-it "7.0.0-0"]
                 [com.stuartsierra/dependency "0.2.0"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :clean-targets ^{:protect false} ["../priv/static/js/compiled" "target"]

  :figwheel {}

  :profiles
  {:dev
   {:dependencies []

    :plugins      [[lein-figwheel "0.5.4-3"]]
    }}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "writisan-client.core/reload"}
     :compiler     {:main                 writisan-client.core
                    :output-to            "../priv/static/js/compiled/app.js"
                    :output-dir           "../priv/static/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main          writisan-client.core
                    :output-to     "../priv/static/js/compiled/app.js"
                    :optimizations :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print  false
                    :externs ["externs/window.js"]}}

    ]})
