(ns text
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
   [clojure.string :as str]))

;; ** Set-up

;; ** Load symbols

(wl/load-all-symbols 'w)

;; *** Portal

(def open-portal (portal/open)) ; Open a new inspector

(add-tap #'portal/submit) ; Add portal as a tap> target

(tap> :hello)


;; Remove Diacritics

(w/RemoveDiacritics "Paweł")

;; Transliterate to Cyrillic

;; Find entity representing Cyrillic writing script
;; see. https://reference.wolfram.com/language/ref/Transliterate.html

(filter #(.contains % "Cyr")
        (map last (w/EntityList (w/EntityValue "WritingScript"))))

(wl/eval
 '(Transliterate "Pawel" (-> Automatic (Entity "WritingScript" "Cyrillic::73gxm"))))

;; Alphabetic order for a given language

(w/AlphabeticOrder "z" "å" (w/Entity "Language" "Swedish"))

(w/AlphabeticSort ["modère", "côtés", "modéré", "côtes"] "French")

(w/TextWords "The investment came from a London-based company.")

;; Alphabet

(w/Alphabet (w/Entity "Language", "Russian"))

;; Text Structure

(p/view '(TextStructure "Tha cat sat on the mat."))

;; Word Data
;; see more: https://reference.wolfram.com/language/ref/WordData.html


(w/WordData "fish")

(w/WordData "fish" "PartsOfSpeech")

(w/WordData "fish" "Synonyms")

(w/WordData "fish" "Definitions")

(w/WordData "fish" "BroaderTerms")

(w/WordStem ["dogs", "cats", "crying", "running"])

;; Interpreting text

(wl/eval '((Interpreter "SemanticNumber") "seventy-five"))
(wl/eval '((Interpreter "University") "oxford u."))
(wl/eval '((Interpreter "Location") "eiffel tower"))

;; Spell checking

(w/SpellingCorrectionList "elefant")

;; Language Identify

(w/LanguageIdentify "la casa es azul")

;; Translate

(wl/eval '(w/WordTranslation "virtuoso", (-> "English" All)))

(map #(w/TextTranslation "Where is the library?" %) ["Russian" "German" "Spanish"])

;; Word Frequency Data

(w/WordFrequencyData ["dog", "cat"])

(/ (w/WordFrequencyData "war")
   (w/WordFrequencyData "peace"))

(p/view '(DateListPlot (WordFrequencyData "economy" "TimeSeries")))

;; Alignment

(wl/eval
 '(SequenceAlignment "abcXXabcXabc", "abcabcYYYabc", (-> Method "Local")))

(wl/eval
 '(SequenceAlignment (BioSequence"DNA", "CGGAGT"), (BioSequence"DNA", "CGTAGT")))

;; Similarity

(w/SmithWatermanSimilarity "xxxxABCx", "yABCyyyy")

(wl/eval
 '(SmithWatermanSimilarity (BioSequence"DNA", "AGGTCCCAAAA"), (BioSequence"DNA", "AGGTTCCAAT")))

(w/DamerauLevenshteinDistance "abc", "cba")

;; Word List

(w/WordList)  ; -- common english words

(wl/eval
 '(WordList "KnownWords", (-> Language "Spanish")))

;; Entity Reckognition cont.

(wl/eval
 '(SemanticInterpretation "congo", (-> AmbiguityFunction All)))
