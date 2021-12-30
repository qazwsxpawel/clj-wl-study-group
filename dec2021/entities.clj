(ns entities
  (:require
   [clojuratica.core :as wl]
   [clojuratica.tools.graphics :as graphics]
   [clojuratica.base.parse :as parse]
   [clojuratica.runtime.defaults :as defaults]
   [clojuratica.base.convert :as convert]
   [clojuratica.base.evaluate :as evaluate]
   [clojuratica.lib.helpers :as h]
   [clojuratica.base.express :as express]
   [portal.api :as portal]
   [clojuratica.tools.portal :as p]
   [clojure.string :as str]))

;; * Entities

;; ** Set-up

;; check, check...
(wl/eval '(Map (fn [x] (+ x 1)) [1 2 3]))
(wl/load-all-symbols 'entities)


;; *** Portal

(def open-portal (portal/open)) ; Open a new inspector

(add-tap #'portal/submit) ; Add portal as a tap> target

(tap> :hello)

(p/view '(BarChart (EntityValue (EntityClass "Planet" All) "Radius")))

(p/view '(GeoImage
          (Entity "City" ["NewYork" "NewYork" "UnitedStates"])))

(p/view '(GeoGraphics
          [Red (GeoPath [(Entity "City" ["Portland" "Oregon" "UnitedStates"])
                         (Entity "City" ["Orlando" "Florida" "UnitedStates"])
                         (Entity "City" ["Boston" "Massachusetts" "UnitedStates"])]
                        "Geodesic")]))

(p/view '(MoleculePlot3D (Molecule "O=C(C1CCC1)S[C@@H]1CCC1(C)C")))

;; *** Previous meet

;; **** Rotating

;; **** US ZIP Codes

;; ** Knowledge Representation

;; *** Wolfram Alpha

;; TODO: Wolfram Alpha Support
;; - [ ] parsing tables
;; - [ ] printed output
;; - [ ] computable output

;; TODO: parsing to idiomatic clojure, more friendly with tooling etc.
;; - [ ] Handling missing
;; - [ ] Handling quantities
;; consistently representing quantites in clojure, is there a small lib for that, e.g.,
;; 54219.90214040854*PowerMiles-2People -> x^-2 People

(def wa-q "miami zip codes")
(def wa-zip (WolframAlpha wa-q))

;; (tap> (WolframAlpha wa-q [["Input", 1] "ComputableData"]))

(defn wa-comp-data [wa-q & {:keys [key-fn val-fn]
                            :or   {key-fn identity
                                   val-fn identity}}]
  (let [pods (wl/eval (WolframAlpha wa-q "PodIDs") {:flags [:custom-parse]
                                                    :parse/custom-parse-symbols ['Quantity Missing]})]
    (into {}
          (map (fn [p] [(key-fn p)
                        (val-fn (wl/eval (WolframAlpha wa-q [[p, 1] "ComputableData"]) {:flags [:custom-parse]
                                                                                        :parse/custom-parse-symbols '[Quantity Missing]}))]) pods))))

(defn forward-fill [wa-table-pod]
  (loop [result []
         header nil
         data wa-table-pod]
    (if-not (seq data)
      (with-meta result
        {:portal.viewer/default :portal.viewer/table})
      (let [row (first data)
            current-header (cond
                             (and (str/blank? (first row)) header) header
                             (not (str/blank? (first row))) (first row)
                             :else nil)]
        (recur (conj result
                     (into [current-header] (rest row)))
               current-header
               (rest data))))))

(defmethod parse/custom-parse 'Quantity [expr opts]
  (pr-str (conj (map parse/parse (.args expr) opts) 'Quantity)) ;; this is OK for display, not so much for computation
  #_(let [[n q] (map parse/parse (.args expr) opts)]
      (with-meta [:div.Quantity n q]
        {:portal.viewer/default :portal.viewer/hiccup})))

(defmethod parse/custom-parse 'Missing [expr opts]
  nil
  ;; could use generic keyword
  #_:wl/missing
  ;; or try to keep as much information as possible, e.g.
  ;; :wl.missing/not-found
  )


(def wa-comp
  (wa-comp-data wa-q :val-fn (fn [v] (try (forward-fill v)
                                          (catch Exception _
                                            (do (prn "Error")
                                                v))))))

(tap> wa-comp)

#_(defn table-parse [table-text]
    (let [[header rows] (str/split table-text #"\n")
          header'       (str/split header #" \| ")
          rows'         (map #(str/split % #" \| ") (str/split rows #"\n"))]
      (map #(zipmap header' %) rows')))

(defn wolfram-alpha-readable [wa-out]
  (into {}
        (map (fn [[_ k v]] [(ffirst k) v])
             wa-out)))

(tap> wa-zip)

(get (wolfram-alpha-readable wa-zip) "Housing:ZIPCodeData")

(tap> (wolfram-alpha-readable wa-zip))

(zipmap ["foo" "bar"] ["thils" "that"])

(GeoEntities
 (Entity "City" ["Miami" "Florida" "UnitedStates"])
 "ZIPCode")

;; this timed out, maybe there is a way to make it work?
;; (GeoEntities
;;  (Entity "Country" "UnitedStates")
;;  "ZIPCode")


;; *** Entities

(def default-opts
  {:flags [:custom-parse]
   :parse/custom-parse-symbols '[Quantity Missing]})

(defn weval [form]
  (wl/eval form default-opts))

;; Documentation
;; https://reference.wolfram.com/language/guide/EntityTypes.html

;; What kinds of entities are available?

(def ents (EntityValue))

;; List all entites for a given entity kind

(def zips (Short (EntityList "ZIPCode")))

;; From natural language to entities

(defn interpreter [text]
  (wl/eval `((Interpreter "SemanticExpression") ~text)))

(interpreter "Paris")
(interpreter "Lake Victoria")
(interpreter "Caffeine molecule")
(interpreter "NYC ZIP Codes")

(wl/eval '((Entity "City" ["Paris" "IleDeFrance" "France"]) "Properties"))
(wl/eval `(~(interpreter "Paris") "Properties"))
(wl/eval '((Entity "City" ["Paris" "IleDeFrance" "France"]) "PopulationDensity"))

(defn remove-nil-vals [m]
  (reduce-kv (fn [m k v]
               (if (nil? v)
                 m
                 (assoc m k v)))
             {}
             m))

(remove-nil-vals (weval `(~(interpreter "Paris") "Association")))

(weval '(Dot [1 2 3] [4 5 6]))
