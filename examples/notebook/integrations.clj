(ns notebook.integrations
  (:require [clojuratica.core :as wl]
            [clojure.string :as str]
            [clojuratica.tools.clerk-helper :refer [view]]
            [nextjournal.clerk :as nb]
            [nextjournal.beholder :as beholder]
            [nextjournal.clerk.webserver :as webserver]
            [clojure.java.browse :refer [browse-url]]))

;; # Clerk

;; ## 🌎 Geo

(view '(GeoGraphics
        [Red (GeoPath [(Entity "City" ["Portland" "Oregon" "UnitedStates"])
                       (Entity "City" ["Orlando" "Florida" "UnitedStates"])
                       (Entity "City" ["Boston" "Massachusetts" "UnitedStates"])]
                      "Geodesic")])
      :folded? true)


;; ## ⌛️ Time

(view '(TimelinePlot
        [(Entity "HistoricalEvent" "WorldWar1")
         (Entity "HistoricalEvent" "WorldWar2")
         (Entity "HistoricalEvent" "VietnamWar")
         (Entity "HistoricalEvent" "KoreanWarBegins")])
      :folded? true)

(view '(GeoGraphics))

(view '(GeoImage (Entity "City" ["NewYork" "NewYork" "UnitedStates"])))


;; ## 🔢 Numbers

(view '(BarChart (EntityValue (EntityClass "Planet" All) "Radius")))

;; ## 😎 3D

(view '(MoleculePlot3D (Molecule "O=C(C1CCC1)S[C@@H]1CCC1(C)C")))

;; ## 📺 Animate!

(view '(Animate (Plot (Sin (+ x a)) [x 0 10]) [a 0 5] (-> AnimationRunning true)))

;; ## Sunday evening family friendly fun

(defn image [[_ img]]
  ((last (last img)) "URL"))

(def movie-ents
   (wl/wl
    '(Map (Function [m] [m (m "Image")])
          (Keys
           (Take
            (Reverse
             (Sort
              (DeleteMissing (MovieData
                              (MovieData ["RandomEntities" 300])
                              "DomesticBoxOfficeGross"
                              "EntityAssociation"))))
            2)))))

(nb/html
 [:div.guess-the-movie
  (into [:div [:center [:h2 "Charades: Guess The Movie 📺"]]]
        (mapv (fn [[m _ :as mdata]]
                [:div.movie {:style {:background-color "rgba(255, 255, 3, 0.07)"
                                     :border-radius "1rem"
                                     :padding "1rem"}}
                 [:center
                  [:h5.title {:style {:font-family "Courier"
                                      :margin-bottom "0.5rem"}}
                   (first (str/split (last m) #"::"))]
                  [:img {:src (image mdata)}]]])
              movie-ents))])

(view '(CommunityGraphPlot
        (Graph (ExampleData ["NetworkGraph" "EurovisionVotes"])
               (--> VertexLabels "Name"))))


#_(comment ;; Start A Clerk Notebook

  ;; evaluate the ns

  ;; start server & watcher

    (do
      (webserver/start! {:port 7777})

      (future
        (let [watch-paths ["notebook/"]]
          (reset! nb/!watcher {:paths watch-paths
                               :watcher (apply beholder/watch-blocking #(nb/file-event %) watch-paths)})))
      (prn "Clerk Started!")
      (browse-url "http://localhost:7777"))

    ;; change something and save!

    (nb/clear-cache!)



  )
