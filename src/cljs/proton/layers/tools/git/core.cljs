(ns proton.layers.tools.git.core
  (:require [proton.layers.core.actions :as actions :refer [state]]
            [proton.lib.helpers :as helpers])
  (:use [proton.layers.base :only [init-layer! get-initial-config get-keybindings get-packages get-keymaps describe-mode]]))

(defmethod init-layer! :tools/git
  [_ {:keys [config layers]}]
  (helpers/console! "init" :tools/git))

(defmethod describe-mode :tools/git [] {})

(defmethod get-keybindings :tools/git
  []
  {:g {:category "git"
       :a {:action "git-plus:add" :title "add files"}
       :A {:action "git-plus:add-all" :title "add all files"}
       :s {:action "atomatigit:toggle" :title "status" :target actions/get-active-editor}
       :S {:category "stash"
           :s {:action "git-plus:stash-save" :title "save"}
           :p {:action "git-plus:stash-pop" :title "pop"}
           :k {:action "git-plus:stash-keep" :title "keep"}
           :d {:action "git-plus:stash-drop" :title "drop"}}
       :P {:action "git-plus:push" :title "push"}
       :p {:action "git-plus:pull" :title "pull"}
       :c {:category "commit"
           :c {:action "git-plus:commit" :title "commit"}
           :C {:action "git-plus:commit-all" :title "commit all"}
           :a {:action "git-plus:commit-amend" :title "amend commit"}}
       :b {:category "branch"
           :c {:action "git-plus:checkout" :title "checkout branch"}
           :C {:action "git-plus:new-branch" :title "create branch"}}
       :B {:action "blame:toggle" :title "blame" :target actions/get-active-editor}
       :L {:action "git-plus:log-current-file" :target actions/get-active-editor :title "log current file"}
       :l {:action "git-plus:log" :title "log project"}
       :h {:action "git-history:show-file-history" :target actions/get-active-editor :title "file history"}
       :d {:category "git diff"
           :n {:action "git-diff:move-to-next-diff" :target actions/get-active-editor :title "next diff"}
           :N {:action "git-diff:move-to-previous-diff" :target actions/get-active-editor :title "previous diff"}
           :l {:action "git-diff:toggle-diff-list" :target actions/get-active-editor :title "list diffs"}}}})

(defmethod get-packages :tools/git
  []
  [:git-plus
   :language-diff
   :atomatigit
   :blame
   :merge-conflicts
   :git-history])

(defmethod get-keymaps :tools/git []
  [{:selector ".atomatigit .file-list-view" :keymap [["s" "atomatigit:stage"]
                                                     ["d" "atomatigit:toggle-diff"]
                                                     ["u" "atomatigit:unstage"]
                                                     ["q" "core:cancel"]]}])

(defmethod get-initial-config :tools/git [] [])

;; Downwards compatibility. Don't use these.
(defmethod get-packages :git [] (get-packages :tools/git))
(defmethod get-keymaps :git [] (get-keymaps :tools/git))
(defmethod get-keybindings :git [] (get-keybindings :tools/git))
(defmethod get-initial-config :git [] (get-initial-config :tools/git))
(defmethod init-layer! :git [] (init-layer! :tools/git))
(defmethod describe-mode :git [] (describe-mode :tools/git))
