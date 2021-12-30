(ns examples
  (:require
   [clojuratica.core :as wl]
   [clojure.repl :as repl]
   [clojuratica.lib.helpers :as h]
   [clojuratica.tools.graphics :as graphics]
   [wl-symbols-loader :refer [load-all-symbols]]
   [clojure.walk :as walk]))

(load-all-symbols (.name *ns*))                  ; From file or dynamically

;; ------------------------------------------------------------------------------
;; * Eval

(eval    '(map (fn       [x] (+ x 1)) [1 2 3]))  ; Clojure Eval
(wl/wl   '(Map (Function [x] (+ x 1)) [1 2 3]))  ; Wolfram Lang. Eval

(wl/wl   '(TextCases "NYC, Los Angeles, and Chicago are the largest cities in the United States of America in 2018.", ["City", "Country", "Date"]))

(comment  ; helpers

  (def canvas (graphics/make-math-canvas! @wl/kernel-link-atom))
  (def app (graphics/make-app! canvas))
  (defn quick-show [clj-form]
    (graphics/show! canvas (wl/->wl! clj-form {:output-fn str})))
  )

;; * MISC

(wl/wl '((Interpreter "City")  "nyc"))           ; =>(Entity "City" ["NewYork" "NewYork" "UnitedStates"])
(wl/wl '(Head [1 2 3]))                          ; => List
(wl/wl '({a b c d} c))                           ; => ((HashMap (-> a b) (-> c d)) c)

;; ------------------------------------------------------------------------------
;; * A Wild Mix of WL & CLJ

(let [n (-> (mapv #(Plus 1 %) [1 2 3])         ; CLJ
            (Dot [3 4 5])                      ; WL
            range                                ; CLJ
            (Take 5)                           ; WL
            Last                               ; WL
            dec)]                                ; CLJ
  (wl/wl `(Take                                  ; WL Eval CLJ Datastructure Representing WL Expression
           (Sort
            (Map
             (Function [gene]
                       [(GenomeData gene "SequenceLength") gene])
             (GenomeData)))
           ~n)))

(-> (mapv #(Plus 1 %) [1 2 3])         ; CLJ
    (Dot [3 4 5])                      ; WL
    range                                ; CLJ
    (Take 5)                           ; WL
    )


;; ------------------------------------------------------------------------------
;; * And now for something completely different

(def wl-symbols (set (map keyword (keys (ns-publics 'w)))))  ;; ... as keywords!

(defn wl-symbol? [sym-or-kw]
  (or (wl-symbols sym-or-kw)
      (wl-symbols (keyword sym-or-kw))))

(defn ->wl-clj [hiccup-style-form]
  (walk/postwalk
   (fn [form]
     (cond

       (and (keyword? form) (wl-symbol? form)) (symbol form)

       (and (vector? form)
            ((some-fn keyword? symbol?) (first form))
            (wl-symbol? (first form))) (list* form)

       :else form))
   hiccup-style-form))

(quick-show
 (->wl-clj
  [:GeoImage
   [:Entity "City" ["NewYork" "NewYork" "UnitedStates"]]]))

(for [c ["London" "Tel Aviv" "Helsinki" "Seattle"]]
  (->> [:CityData c "Population"]
      ->wl-clj
      wl/wl))

(let [t [:Snippet
         [:ContentObject
          [:ExampleData ["Text" "AliceInWonderland"]]]]]
  (-> [:WordFrequency t [:TextWords t]]
      ->wl-clj wl/wl))


;; ------------------------------------------------------------------------------
;; * Anything Else?

(WolframAlpha "eiffel tower height")

(WolframAlpha "US population by ZIP code")
