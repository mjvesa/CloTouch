(ns clotouch
  (:import (com.vaadin.addon.touchkit.ui Switch NumberField NavigationView VerticalComponentGroup)
				  (com.vaadin.ui Button Button$ClickListener TextField CssLayout)))

(use '[clojure.tools.nrepl.server :only (start-server)])
 
(defn construct-view []
  "Constructs the main view"
    (let [view (NavigationView.)
          content (CssLayout.)
          group (VerticalComponentGroup.)]
    (doto view 
      (.setCaption "Clojure application")
      (.setContent content)
      (.setRightComponent (Button. "OK")))
    (doseq [components [(TextField. "Planet")
                        (NumberField. "Found")
                        (Switch. "Probed")]]
      (.addComponent group components))
    (.addComponent content group)
    (def clotouch/group group)
    view))

(defn main [ui] 
  "Entry point to our program"
    (let [clickListener (proxy [Button$ClickListener] []
								          (buttonClick [event] 
								            (start-server :port 7888))) 
          view (construct-view)
          button (Button. "Start REPL")]                
    (.addListener button clickListener)
    (.addComponent (.getContent view) button)
    (.addComponent (.getContent view) (Button. "refresh view"))
    (.setContent ui view)))