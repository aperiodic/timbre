(ns taoensso.timbre.appenders.postal
  "Email appender. Depends on https://github.com/drewr/postal."
  {:author "Peter Taoussanis"}
  (:require [clojure.string  :as str]
            [postal.core     :as postal]
            [taoensso.timbre :as timbre]))

(def postal-appender
  {:doc (str "Sends an email using com.draines/postal.\n"
             "Needs :postal config map in :shared-appender-config, e.g.:
             ^{:host \"mail.isp.net\" :user \"jsmith\" :pass \"sekrat!!1\"}
             {:from \"Bob's logger <me@draines.com>\" :to \"foo@example.com\"}")
   :min-level :error :enabled? true :async? true
   :limit-per-msecs (* 1000 60 10) ; 1 subject / 10 mins
   :fn (fn [{:keys [ap-config prefix throwable args]}]
         (when-let [postal-config (:postal ap-config)]
           (let [[subject & body] args]
             (postal/send-message
              (assoc postal-config
                :subject (str prefix " - " (or subject throwable))
                :body    (str (str/join \space body)
                              (timbre/stacktrace throwable "\n")))))))})