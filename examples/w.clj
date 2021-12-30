(ns w
  (:require
   [clojuratica.core :as wl]
   [clojuratica.tools.graphics :as graphics]
   [clojuratica.base.parse :as parse :refer [custom-parse]]
   [wl-symbols-loader :refer [load-all-symbols]]
   [clojuratica.lib.helpers :as h]))

"Hello Everyone!"

;; * Syntax

;; RulePlot[CellularAutomaton[30]]
;; (RulePlot (CellularAutomaton 30))


;; * Eval

(eval    '(map (fn       [x] (+ x 1)) [1 2 3]))
(wl/eval '(Map (Function [x] (+ x 1)) [1 2 3]))

(wl/eval "Map[Function[{x}, x + 1], {1, 2, 3}]")

;; |/////////////////////////|
;; |Convert >> Eval >> Parse |
;; |/////////////////////////|


;; * Intern

;; ** def

(def P (parse/parse-fn 'Plus {:kernel/link @wl/kernel-link-atom}))

(def greetings
  (wl/wl
   '(Function [x] (StringJoin "Hello, " x "! This is a Mathematica function's output."))))

;; ** intern

(wl/clj-intern 'Plus {})

(map wl/clj-intern ['Dot 'Plus])

(load-all-symbols (.name *ns*))  ; load symbols from file

;; OR load dynamically
;; (wl/load-all-symbols 'w)
;; (wl/load-all-symbols (.name *ns*))


;; * REPL

(require '[clojure.repl :as repl])

(repl/doc w/GeoGraphics)

(repl/find-doc "two-dimensional")

(repl/apropos #"(?i)geo")

(h/help! 'Axes)

(h/help! '(Take
           (Sort
            (Map
             (Function [gene]
                       [(GenomeData gene "SequenceLength") gene])
             (GenomeData)))
           n)
         :return-links true)

(Information 'GenomeData)

(wl/wl '((WolframLanguageData "GenomeData") "Association"))


;; * Graphics

;; Init
(def canvas (graphics/make-math-canvas! @wl/kernel-link-atom))
(def app (graphics/make-app! canvas))
(defn quick-show [clj-form]
  (graphics/show! canvas (wl/->wl! clj-form {:output-fn str})))

(quick-show '(ChemicalData "Ethanol" "StructureDiagram"))
(quick-show '(GridGraph [5 5]))
(quick-show '(GeoImage (Entity "City" ["NewYork" "NewYork" "UnitedStates"])))


;; * Custom Parse

(wl/wl '(Hyperlink "foo" "https://www.google.com"))

(defmethod custom-parse 'Hyperlink [expr opts]
  (-> (second (.args expr))
      (parse/parse opts)
      java.net.URL.))

(wl/wl '(Hyperlink "foo" "https://www.google.com") {:flags [:custom-parse]
                                                    :parse/custom-parse-symbols ['Hyperlink]})


;; * More

;; WordFrequency[ExampleData[{"Text", "AliceInWonderland"}], {"a", "an", "the"}, "CaseVariants"]
