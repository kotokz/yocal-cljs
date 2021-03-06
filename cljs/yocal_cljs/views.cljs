(ns yocal-cljs.views
  (:require [re-frame.core :as re-frame]
            [re-com.util :refer [item-for-id]]
            [re-com.core :refer [h-box hyperlink-href v-box title]]
            [clojure.string :as str]
            [yocal-cljs.view.login :as login]
            [yocal-cljs.view.game_board :as game]
            [yocal-cljs.view.signup :as signup]))



;; -------------navigator----
(defn active-panel? [panel-name active]
  (if (= panel-name active) "active" ""))

(defn desktop-nav [id]
  [:div {:class "navbar navbar-inverse"}
   (let [jwt (:jwt @(re-frame/subscribe [:user]))]
     [h-box
      :children [
                 [:div {:class "navbar-header"}
                  [:button {:id       "mobile-menu-button"
                            :class    "navbar-toggle pull-left"
                            :on-click #(pr "button cliked")}
                   [:span {:class "icon-bar"}]
                   [:span {:class "icon-bar"}]
                   [:span {:class "icon-bar"}]]]
                 [:div.navbar-collapse.collapse
                  (if (str/blank? jwt)
                    [:ul.nav.navbar-nav
                     [:li {:class (active-panel? :login-panel id)} [:a {:href "#/"} "Login"]]
                     [:li {:class (active-panel? :signup-panel id)} [:a {:href "#/signup"} "Signup"]]]
                    [:ul.nav.navbar-nav
                     [:li {:class (active-panel? :home-panel id)} [:a {:href "#/home"} "Home"]]
                     [:li {:class (active-panel? :about-panel id)} [:a {:href "#/about"} "About"]]])]]])])


;; --------------------
(defn about-title []
  [title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [v-box
   :gap "1em"
   :children [[about-title]
              [link-to-home-page]
              (let [jwt (:jwt @(re-frame.core/subscribe [:user]))]
                [:button.btn.btn-default {:on-click #(re-frame/dispatch [:get-balance jwt])}
                 "GetBalance"])]])
;; --------------------
(defn login-panel []
  [v-box
   :gap "1em"
   :align :center
   :children [[login/form-page]]])
;; --------------------

(defn signup-panel []
  [v-box
   :gap "1em"
   :align :center
   :children [[signup/signup-form-page]]])

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [game/game-board])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :login-panel [] [login-panel])
(defmethod panels :signup-panel [] [signup-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [v-box
       :height "100%"
       :size "auto"
       :gap "10px"
       :children [[desktop-nav @active-panel]
                  (panels @active-panel)]])))
