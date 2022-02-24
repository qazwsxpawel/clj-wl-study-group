(ns engine
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
   [clojuratica.tools.clerk-helper :as c]
   [clojure.string :as str]
   [clojure.repl :as repl]))

;; * FEB 2022

;; ** Set-up

;; check, check...
(wl/eval '(Map (fn [x] (+ x 1)) [1 2 3]))

(defn load-all-symbols-from-file [ns-sym & {:keys [filename]
                                            :or   {filename "wld.wl"}}]
  (wl/eval (let [path-string (.toString (.getParent (io/file (io/resource filename))))]
             `(AppendTo $Path ~path-string)))
  (doall (->> '(Get "wld.wl")
              wl/eval
              (map vec)
              (map (fn [[sym doc]]
                     (wl/clj-intern (symbol sym) {:intern/ns-sym ns-sym
                                                  :intern/extra-meta {:doc doc}}))))))

(load-all-symbols-from-file 'scratch.feb)

(time (wl/load-all-symbols 'engine))

(def ents (take-last 10 (wl/eval '(WolframLanguageData))))

(wl/eval `(Map (Function [e] (e "Name"))
               (WolframLanguageData)))

(doseq [e (wl/eval '(WolframLanguageData))]
  (prn (wl/eval `(~e "Name")) " : " (count (wl/eval `(~e "PlaintextUsage")))))

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
