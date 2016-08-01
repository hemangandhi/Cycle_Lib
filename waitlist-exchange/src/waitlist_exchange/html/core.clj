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
         [:li [:a.btn.btn-warning {:href "logout"} "Logout"]]
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
                [:input#email-entry.form-control {:type "email" :name "email" :placeholder "Email address" :required true}]]
               [:div.form-group
                [:label.sr-only {:for "passwd-entry"} "Password"]
                [:input#email-entry.form-control {:type "password" :name "password" :placeholder "Password" :required true}]]
               [:div.form-group
                [:button.form-control.btn.btn-primary {:type "submit"} "Login"]]]]]]]]])]]])

(defn gen-page [is-logged-in uname & {:keys [body-fn] :or {body-fn (fn [] (vec [:p "test"]))}}]
  (h/html5 (if is-logged-in (head-tag uname) (head-tag))
           [:body (concat [(navbar is-logged-in)]
                          [(body-fn)])]))

(defn make-acc-body [& {:keys [more-info] :or {more-info [:p "Please provide the follwing"]}}]
  [:div more-info
         [:form.form {:role "form" :method "post" :action "submit-account"}
          [:div.form-group
           [:label.sr-only {:for "email-mk"} "Enter your email:"]
           [:input#email-mk.form-control {:type "email" :name "email" :placeholder "Your email" :required true}]]
          [:div.form-group
           [:label.sr-only {:for "name-mk"} "Enter your username:"]
           [:input#name-mk.form-control {:type "text" :name "name" :placeholder "Your name" :required true}]]
          [:div.form-group
           [:label.sr-only {:for "password-mk"} "Enter your password:"]
           [:input#password-mk.form-control {:type "password" :name "password" :placeholder "Password" :required true}]]
          [:div.form-group
           [:label.sr-only {:for "password-conf"} "Confirm your password"]
           [:input#password-conf.form-control {:type "password" :placeholder "Confirm password" :required true}]]
          [:div.form-group
           [:button.form-control.btn.btn-primary {:type "submit"} "Make an Account!"]]]])

(defn login-fail-body []
  [:p "We could not find that account!"
   [:a {:href "/mk-account"} "Don't have an account?"]])
