(require '[cljs.closure :as closure])

(if (= 1 (count *command-line-args*))
  (closure/build (first *command-line-args*) {:optimizations :simple ;;TODO: Advanced compilation chokes on SVG handling in D3
                                              :foreign-libs [{:file "vendor/d3/d3.js"
                                                              :provides ["jsd3.core"]}]
                                              :output-dir "resources/public/out"
                                              :output-to "resources/public/main.js"})
  (println "compile.clj requires one argument: path to cljs file to compile"))
