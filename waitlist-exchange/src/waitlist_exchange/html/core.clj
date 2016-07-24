(ns waitlist-exchange.html.core
  (:require [hiccup.page :as h]
            [hiccup.element :as e]))

(def bootstrap-css-cdn "https://maxcdn.bootstrapcdn.com/bootswatch/3.3.6/flatly/bootstrap.min.css")
(def bootstrap-js-cdn "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js")
(def jquery-cdn "https://code.jquery.com/jquery-2.2.4.min.js")

(defn head-tag
  ([] [:head
       [:link {:rel "stylesheet" :href bootstrap-css-cdn}]
       [:script {:src jquery-cdn}] [:script {:src bootstrap-js-cdn}]
       [:title "Hello there!"]])
  ([uname] [:head
            [:link {:rel "stylesheet" :href bootstrap-css-cdn}]
            [:script {:src jquery-cdn}] [:script {:src bootstrap-js-cdn}]
            [:title (str "Welcome, " uname)]]))

(defn navbar [is-logged-in]
  [:nav.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:button.navbar-toggle.collapsed {:type "button" :data-toggle "collapse" :data-target "#navbar-content"}
      (concat [[:span.sr-only "Toggle navigation"]]
              (for [x (range 3)] [:span.icon-bar]))
     [:a.navbar-brand {:href "/"} "WLE"]]]
    [:div#navbar-content.collapse.navbar-collapse
      (if is-logged-in
        [:ul.navbar-nav.nav
         [:li.active [:a {:href "/my-trades"} "Possible Trades"]]
         [:li [:a {:href "/add-info"} "Add Requests"]]]
        [:ul.navbar-nav.nav
         [:li.dropdown
          [:a.dropdown-toggle {:data-toggle "dropdown" :href "#"} [:b "login"] [:span.caret]]
          [:ul.dropdown-menu
           [:li [:a {:href "/mk-account"} "Make an account"]]
           [:li
            [:div.row
             [:div.col-md-12
              [:form.form {:role "form" :method "post" :action "login"}
               [:div.form-group
                [:label.sr-only {:for "email-entry"} "Email Address"]
                [:input#email-entry.form-control {:type "email" :placeholder "Email address" :required true}]]
               [:div.form-group
                [:label.sr-only {:for "passwd-entry"} "Password"]
                [:input#email-entry.form-control {:type "password" :placeholder "Password" :required true}]]
               [:div.form-group
                [:submit.form-control.btn.btn-primary "Login"]]]]]]]]])]]])

(defn gen-page [is-logged-in uname]
  (h/html5 (if is-logged-in (head-tag uname) (head-tag))
           [:body (concat [(navbar is-logged-in)]
                          [[:p "Test"]])]))
