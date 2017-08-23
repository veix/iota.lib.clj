(ns clota.client
  (:require  
    [clojure.spec.alpha :as s]
    [cheshire.core :as json]
    [clj-http.client :as client]))


(def fields
  #{"seed" "address" "message" "transaction" "trytes" "hash" "bundles" "addresses" "tags" "approvees" "securityLevel" "minWeightMagnitude" "uris"})

(def iota-localhost "http://localhost:14265")

;; from https://iota.readme.io/docs

(defn get-iota-version [] (json/encode {:version "1.2.0"}))

(defn- build-iota-req
  "params 
    - command { String }
    - params { Map } Optional "
  [{:keys [host command] :or {host iota-localhost}} & [params]]
  (let [body (json/encode (merge {:command command} (or params {})))
        req (try (client/post (str host)
                              {:body body
                               :content-type :json })
                 (catch Exception e (ex-data e)))]
    (-> req
        :body
        (json/decode true))))

(defn get-node-info 
  "@param { String } host" 
  [host]          
  (build-iota-req {:host host
                   :command "getNodeInfo"}))

(defn get-neighbors 
  "@param { String } host" 
  [host] 
  (build-iota-req {:host host
                   :command "getNeighbors"}))

(defn add-neighbors 
  "@param { String } host 
   @param { Vector of Strings } uris"
 [host [uris]] 
  (build-iota-req {:host host
                   :command "addNeighbors"}
                  {:uris [uris]}))

(defn remove-neighbors
  "@param { String } host 
   @param { Vector of Strings } uris" 
  [host [uris]] 
  (build-iota-req {:host host
                   :command "removeNeighbors"}
                  {:uris [uris]}))

(defn get-tips 
  "@param { String } host" 
  [host] 
  (build-iota-req {:host host
                   :command "getTips"}))

(defn find-transactions 
  "find-transactions can take as inputs a map of one or more of:
    - bundles [...] : List of bundle hashes. The hashes needs to be extended to 81 trytes by padding the hash with 9's.
    - addresses [...]  : List of addresses.
    - tags [...] : List of tags. Has to be 27 trytes. 
    - approvees [...] : List of approvee transaction hashes.  
  
   Note: Including more than one input type returns the intersection of these values.
  
   e.g. (find-transaction host {:addresses [AA9... AB9...]}d
   @param { String } host 
   @param { Map } :bundles :addresses :tags :approvees" 
  [host inputs] 
  (build-iota-req {:host host
                   :command "findTransactions"}
                  inputs))
 
(defn get-trytes 
  "@param { String } host
   @param { Vector of Strings } trytes : List of transaction hashes of which you want to get trytes from."
  [host [trytes]] 
  (build-iota-req {:host host
                   :command "getTrytes"}
                  {:hashes [trytes]}))

(defn get-inclusion-states 
  "@param { String } host" 
  [host {:keys [transactions tips] :as params}] 
  (build-iota-req {:host host
                   :command "getInclusionStates"}
                  params))

(defn get-balances 
  "@param { String } host" 
  [host {:keys [addresses threshold] :as params}] 
  (build-iota-req {:host host
                   :command "getBalances"}
                  params))

(defn get-transactions-to-approve 
  "@param { String } host
   @param { Map } :depth" 
  [host {:keys [depth] :as params}] 
  (build-iota-req {:host host
                   :command "getTransactionsToApprove"}
                  params))

(defn attach-to-tangle 
  "@param { String } host 
   @param { Map } :trunk-transaction :branch-transaction :min-weight-magnitude :trytes)" 
  [host {:keys [trunk-transaction branch-transaction min-weight-magnitude trytes] :as params}] 
  (build-iota-req {:host host
                   :command "attachToTangle"}
                  {:trunkTransaction trunk-transaction
                   :branchTransaction branch-transaction
                   :minWeightMagnitude min-weight-magnitude
                   :trytes trytes}))

(defn interrupt-attaching-to-tangle  
  "@param { String } host" 
  [host] 
  (build-iota-req {:host host
                   :command "interruptAttachingToTangle"}))

(defn broadcast-transactions
  "@param { String } host 
   @param { Vector of Strings } transactions : List of raw data of transactions to be rebroadcast."
  [host [transactions]] 
  (build-iota-req {:host host
                   :command "broadcastTransactions"}
                  {:trytes [transactions]}))

(defn store-transactions  
  "@param { Vector } transactions : List of raw data of transactions to store locally."
  [host [transactions]] 
  (build-iota-req {:host host
                   :command "storeTransactions"}
                  {:trytes [transactions]}))

