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
