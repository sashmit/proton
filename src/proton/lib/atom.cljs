(ns proton.lib.atom
  (:require [proton.lib.helpers :refer [generate-div process->html]]
            [cljs.nodejs :as node]
            [clojure.string :as string :refer [lower-case upper-case]]))

(def commands (.-commands js/atom))
(def workspace (.-workspace js/atom))
(def keymaps (.-keymaps js/atom))
(def views (.-views js/atom))
(def config (.-config js/atom))

(def element (atom (generate-div "test" "proton-which-key")))
(def bottom-panel (atom (.addBottomPanel workspace
                                       (clj->js {:visible false
                                                  :item @element}))))

(def modal-element (atom (generate-div "test" "proton-modal-panel")))
(def modal-panel (atom (.addModalPanel workspace (clj->js {:visible false
                                                           :item @modal-element}))))

(defn update-bottom-panel [html] (aset @element "innerHTML" html))
(defn update-modal-panel [html] (aset @modal-element "innerHTML" html))
(defn show-modal-panel [] (.show @modal-panel))
(defn hide-modal-panel [] (.hide @modal-panel))
(defn show-bottom-panel [] (.show @bottom-panel))
(defn hide-bottom-panel [] (.hide @bottom-panel))

(def steps (atom []))
(defn insert-process-step [text]
  (swap! steps conj text)
  (update-modal-panel (process->html @steps)))

(defn activate-proton-mode! []
  (.log js/console "----> Proton Chain activated!")
  (let [editors (.getTextEditors workspace)]
      (doseq [editor editors]
        (let [view (.getView views editor)
              classList (.-classList view)]
            (.remove classList "vim-mode")
            (.add classList "proton-mode")
            (show-bottom-panel)))))

(defn deactivate-proton-mode! []
  (.log js/console "----> Proton Chain deactivated!")
  (let [editors (.getTextEditors workspace)]
      (doseq [editor editors]
        (let [view (.getView views editor)
              classList (.-classList view)]
            (.remove classList "proton-mode")
            (.add classList "vim-mode")
            (hide-bottom-panel)))))

(defn eval-action! [tree sequence]
  (println "evalling")
  (let [action (get-in tree (conj sequence :action))]
    (.dispatch commands (.getView views workspace) action)
    (deactivate-proton-mode!)))

(defn get-all-settings []
  (let [config-obj (.getAll config)
        parsed-config (atom [])]
    ;; First level are different selectors. The most interesting one being '*'
    (goog.object/forEach config-obj
      (fn [subobj _ _]
        ;; second level are the actual config objects inside the selectorso
        ;; .value holds the actual configuration object for the selector
        (goog.object/forEach (.-value subobj)
          (fn [obj config-prefix _]
            ;; third level is the actual config string. Like core.{xxx}
            (goog.object/forEach obj
              (fn [val config-postfix _]
                (swap! parsed-config conj (str config-prefix "." config-postfix))))))))
    @parsed-config))

(defn set-config! [selector value]
  (.set config selector value))

(defn unset-config! [selector]
  (.unset config selector))
