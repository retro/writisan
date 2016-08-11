(ns writisan-client.edb
  (:require [entitydb.core]
            [entitydb.util])
  (:require-macros [writisan-client.edb :refer [defdbal]]))

(defdbal {:users {}})

