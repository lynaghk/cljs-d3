            _   _                  _  _____ 
       ___ | | (_) ___          __| ||___ / 
      / __|| | | |/ __| _____  / _` |  |_ \ 
     | (__ | | | |\__ \|_____|| (_| | ___) |
      \___||_|_/ ||___/        \__,_||____/ 
             |__/                           

     Data driven documents, now with more Lisp!
     http://keminglabs.com/cljs-d3/


Cljs-d3 is a ClojureScript façade for the [D3](http://mbostock.github.com/d3/) JavaScript DOM-manipulation library.
It transparently coerces ClojureScript data types into the appropriate JavaScript objects and provides a more Clojure-like API to some of the D3 functions:


```clojure
(ns sample.main
  (:require [cljs-d3.core :as d3]
            [cljs-d3.scale :as scale])
  (:require-macros [cljs-d3.macros :as d3m]))

(defn rand [] ((.random js/Math)))

(let [Width       300 ;;Width in pixels
      n           100 ;;Number of data
      scale       (scale/linear :domain [0 1] :range [0 Width])

      sample-data (for [_ (range n)]
                    {:x (rand)
                     :y (rand)
                     :class (if (> (rand) 0.5)
                              "A" "B")})

      scatterplot (d3m/-> d3/d3 (select "#example")
                          (append "svg:svg")
                          (style {:border "2px solid darkGray"
                                  :border-radius 8})
                          (attr {:width  Width
                                 :height Width}))

      points      (d3m/-> scatterplot
                          (selectAll "circle.num")
                          (data sample-data)
                          (enter)(append "svg:circle")
                          (attr {:class "num"
                                 :r 5
                                 :fill #(condp = (:class %)
                                            "A" "darkRed"
                                            "B" "darkBlue")
                                 :cx #(scale (:x %))
                                 :cy #(scale (:y %))}))

      ])
```

For more details and examples, see [http://keminglabs.com/cljs-d3/](http://keminglabs.com/cljs-d3/).

Install
=======

Checkout the repository,

```bash
mkdir vendor
git submodule add https://github.com/lynaghk/cljs-d3 vendor/cljs-d3
```

And then add the sources to your classpath.
If you are using `cake`, for instance, open up `<your project>/.cake/config` and add the line

    project.classpath = vendor/cljs-d3/src/clj:vendor/cljs-d3/src/cljs

And then in your ClojureScript files just

```clojure
(:require [cljs-d3.core :as d3])
```

Experimentation / Development
=============================

If you want to work on cljs-d3 itself or just want a sandbox to play in, this repository has everything you need.
First run

    git submodule update --init

to get D3 and the ClojureScript compiler as git submodules, then bootstrap-install Clojurescript: 

    cd vendor/clojurescript
    ./script/bootstrap

and add its jars to your classpath.
This repository includes a `.cake/config` with the appropriate path.

Then you can use the `compile.clj` script to compile ClojureScript examples, which you can view locally:

```bash
cake run compile.clj samples/scatterplot.cljs
google-chrome resources/public/index.html
```

Pull Requests
=============

Pull requests are welcome; if you want to discuss something before you code it up, feel free to open an issue or send me a [github msg](https://github.com/inbox/new/lynaghk).


Todo
====

We are writing façades as we need them, so some D3 functions may be missing.
If you want something, please send us a note or pull request.


Thanks
======
Mike Bostock for D3 and the Clojure core team for their rad work on Clojure(Script).

This project is sponsored by [Keming Labs](http://keminglabs.com), a technical design studio specializing in data visualization.
