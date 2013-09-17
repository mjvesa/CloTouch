Using Vaadin and TouchKit with Clojure
======================================

Clojure is a modern Lisp for the JVM. In this article  we'll look at using
TouchKit and Clojure from a beginners perspective. Well be using tools and
environment familiar to people who are used to developing Vaadin applications in
Java.

An existing TouchKit example from the Book of Vaadin will be used, which should
make understanding the Clojure parts a bit easier.

Finally, the subject of REPLs is briefly discussed with working examples.

Setting up a bare bones project
===============================

We will be using maven to create an initial TouchKit project and to include
the Clojure jar into that. We will then import the project to Eclipse
and install Counterclockwise for some IDE features.

First, let's use maven to create an empty TouchKit app by executing this in a
cli:
    
        mvn archetype:generate \
        -DarchetypeGroupId=com.vaadin \
        -DarchetypeArtifactId=vaadin-archetype-touchkit \
        -DarchetypeVersion=3.0.0-beta1 \
        -Dpackaging=war \
        -DgroupId=org.example \
        -DartifactId=CloTouch


There might be a newer version of TouchKit, in which case the archetypeVersion
needs to be updated. 

Naturally maven needs to be available in the path for this to work.
Alternatively this can be done from Eclipse, and the following paragraph can be
then be skipped.

Import the resulting project into Eclipse by finding the import.. item
from the file menu or from the context menu obtained by right-clicking in
Project Explorer. Choose Maven -> Existing Maven Projects and choose the
project that was just created.

To install Counterclockwise go to the Help menu, choose Eclipse Marketplace...
and search for Counterclockwise. Install it by clicking the install button.

Next we should add Clojure to our project. Find the pom.xml file in the
project root. Open that file and find the part which describes dependencies.
Those are contained between `<dependency>` tags. Insert the following:
       
    <dependency>
        <groupId>org.clojure</groupId>
        <artifactId>clojure</artifactId>
        <version>1.5.1</version>
    </dependency>

Saving the file should now result in automatic inclusion of the Clojure
jar.

Now the project is set up. The next step is to write some code
which allows us to enter the Clojure realm. But first, a small tutorial.

Clojure basics
==============

Clojure is a dynamically typed language that belongs to the Lisp family.
Its syntax is in prefix form instead of the usual infix found in Java and
many other popular languages. This means that the operation to be performed
comes before the parameters for that operation. Java and many other languages
are mixed infix and prefix: most mathy things are done by using an infix
notation and calling functions uses a prefix form. In Clojure everything is
prefix, which is more uniform. So a function invocation in Clojure is written
as:

        (function param1 param2 ...)

Instead of

        function(param1, param2, ...)

Note how the parameters are separated by white space instead of a colon.
A colon can also be used, but it is simply considered to be white space.

Here's a simple example of arithmetic in Clojure:

        (+ (- 3 2) (* 5 6))

Here 2 is subtracted from 3, 5 is multiplied by 6 and then the result
of both operations are added together.

Instead of packages, Clojure has namespaces:

        (ns my-namespace)

This defines a namespace called my-namespace. This also demonstrates
the common use of a dash to separate words instead of the usual 
CamelCase.

Importing is done in a similar way as in Java, at least for regular Java
classes. Importing in Clojure is a bit more flexible than in Java. Importing
can be done directly in code, or inside a namespace definition. The latter
way is preferred. In this example Button and TextField are imported:

        (ns my-namespace
            (:import (com.vaadin.ui Button TextField)))

The package is defined first, and then the classes to be imported from that
package.

Functions are defined in Clojure as follows:

        (defn my-add [x y]
            (+ x y))

This defines a function my-add, which takes two parameters x and y. The
parameters are defined in a vector, which is denoted by square brackets. Unlike
many other Lisps, Clojure uses various types of brackets in addition to the
usual parenthesis to denote different kinds of data structures.

Let binds values to names. These names are called symbols. Let uses a vector of
symbol and value pairs to connect values to symbols. Let also has a scope,
which is its body, or the block it defines:
        
        (let [a 1
              b 2] 
          (+ a b))

This is just a complex way of saying 1 + 2. Here a and b are available until
the last closing parenthesis. The values defined with let are immutable,
and cannot be changed after defined.

We've now seen the bare minimum of Clojure needed to understand the examples
in this tutorial. Next we'll start using Clojure for real and write our first
example program.

Entering Clojure
================

Unlike some other tutorials on using Vaadin with Clojure, we are not going
to try and write everything in Clojure. To avoid the culture shock, we will
rather pass control to Clojure from a regular Vaadin app written in Java.
This also has the benefit of allowing us to use tools that most developers
developing with Vaadin are familiar with.

Let's first create a simple Clojure file to execute from java. Create a new
directory src/main/clojure. Add that directory to the build path and create a
file by the name clotouch.clj. Paste the following inside that file:

        (ns clotouch
          (:import (com.vaadin.ui CssLayout Label))

        (defn main [ui] 
          (let [layout (new CssLayout)]
            (.setContent ui layout)
            (.addComponent layout 
              (new Label "hello world from clojure"))))

This defines the namespace clotouch, and in it a single function main, which
takes ui as parameter. The parameter ui is our UI instance.

The let block assigns a new CssLayout to layout. `(new <ClassName>)` returns
a new instance of that class. An alternative form would be `(ClassName.)`

Inside the let block we first set the content of `ui` to be `layout`. After
that a Label is added to layout.

Replace the content of the init method in MyVaadinUI.java with this:

        @Override
        protected void init(VaadinRequest request) {
            try {
                RT.loadResourceScript("clotouch.clj");
                RT.var("clotouch", "main").invoke(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

First the cloutuch.clj source code is loaded. Then the main function in
the clotouch namespace is obtained and then invoked. The invoke() method gets
the current UI instance as parameter.

That's it. Execute `mvn install jetty:run` to run the app. It can then be
found at `http://localhost:8080/CloTouch`

To move forward with the TouchKit theme of this tutorial, we will continue
with a more involved example.

A slightly more complicated example
===================================

Let's reproduce a simple program from the Book of Vaadin. Chapter 22.1 Overview
contains such a program. Replace the contents of clotouch.clj with the following
(or just create a new .clj file and modify the code invoking it accordingly):

        (ns clotouch
          (:import (com.vaadin.addon.touchkit.ui Switch NumberField
                    NavigationView VerticalComponentGroup))
                   (com.vaadin.ui Button TextField CssLayout))

        (defn construct-view []
           (let [view (NavigationView.)
                 content (CssLayout.)
                 group (VerticalComponentGroup.)]
             (doto view 
                 (.setCaption "Clojure application")
                 (.setContent content)
                 (.setRightComponent (Button. "OK")))
             (doto group
                 (.addComponent (TextField. "Planet"))
                 (.addComponent (NumberField. "Found"))
                 (.addComponent (Switch. "Probed")))
             (.addComponent content group)
             view))

        (defn main [ui] 
            (.setContent ui (construct-view)))


The code is quite straight-forward. in construct-view, first we create
some components using let. The values
bound to symbols in our let block are new objects. Their default constructor
is called by appending a period to the end of the fully qualified class name.
This is the alternative form for creating new instances described in the
previous chapter.

The body of the function is contained within that aforementioned let block,
which is in the scope of the let block. The macro doto is used to 
invoke a list of functions on the specified object. For example:

        (doto view
          (.setCaption "caption"))

This sets the caption of view to "caption". This form makes long lists
of method invocations on the same object shorter.

At the end of the construct-view function we have view. It evaluates to itself,
and since it is the last thing in the function, it is returned as its value.

Using functional features to make the example more compact
==========================================================

There's some repetition in our example that could be removed by using some
common functional programming features. First, adding components to group
could be replaced with doseq:

        (doseq [components [(TextField. "Planet")
                            (NumberField. "Found")
                            (Switch. "Probed")]]
          (.addComponent group components))

Less parens and less repetition. What doseq does is it applies the function
`(.addComponent group components)` to the list following it. Map does 
almost the same thing, but cannot be used here. Map is lazy, which means its
results are obtained only when they are accessed, and we need the side-effects
from .addComponent. Doseq is not lazy and works here.

The doto form returns the object it is invoking methods on, so more
lines can be saved in construct-view:

             (doto (NavigationView.)
                 (.setCaption "Clojure application")
                 (.setContent (.addComponent (CssLayout.)
                                (doto (VerticalComponentGroup.)
                                    (.addComponent (TextField. "Planet"))
                                    (.addComponent (NumberField. "Found"))
                                    (.addComponent (Switch. "Probed"))))
                 (.setRightComponent (Button. "OK"))))


This a good example how local variables can disappear when programming in a
more functional style. Notice also how this version of construct-view reflects
the structure of the UI. It's as if the UI had been declared in XML instead
of written as executable code.

Using the REPL for UI experiments
=================================

The REPL is often mentioned in discussion about Lisp. The Read Eval Print Loop
can allow Vaadin UIs to be modified without the usual compile and deploy
cycle. In fact it is not even necessary to reload the program in the browser,
as an existing session can be hooked into and modified in place. This
can allow very quick experimentation with different parts of the UI.

There is one caveat though: the modified parts have to be available in some
namespace so they can be accessed. 

Let's use our last example to show how the REPL can be used for such
modifications. We'll be using nREPL, which is network REPL for Clojure.
Counterclockwise is able to connect to such REPLs out of the box.

To add nREPL to our project a dependency is needed in pom.xml:

        <dependency>
            <groupId>org.clojure</groupId>
            <artifactId>tools.nrepl</artifactId>
            <version>0.2.2</version>
        </dependency>

The server needs to be started somewhere in our program. A good choice would
be to place a button somewhere so that the server is only started when
needed.

The VerticalComponentGroup in our example could be exposed, which would allow
adding more items to it. What needs to be done is to define the group
in the clotouch namespace. This can be done as follows:

        (def clotouch/group group)

This needs to be inserted to a place where group is available, which is in
the let block. Def defines a Var, which can be referenced in the namespace.
So using the following will work in the REPL:

        (.addComponent clotouch/group (TextField. "Enter some stuff"))
    
Next we need to start the nREPL server. First we should import the necessary
functions from the nREPL namespace. 

        (use '[clojure.tools.nrepl.server :only (start-server)])

The start-server function can be used to start the server. Starting itself
can be done as follows:

        (start-server :port 7888)

The only thing left is to add a button for starting the server. For the button
we also need a function that will be activated when the button has been clicked.
For that we need to extend Button.ClickListener. This is done in Clojure by
using the proxy macro. Here is the complete main function:

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

Changes to the UI done in the REPL do not become immediately visible. One way
to refresh the view is to add a Button, which is done in the example.

To connect to the new REPL, go to the Window drop down and choose "Connect to
REPL". The default repl should work with our example, so just press OK.

In the REPL the full Clojure language is available. CTRL+UP/CTRL+DOWN can be
used to navigate history. Multiline editing is possible, as pressing enter
at the end of the whole expression entered takes effect.

Let's try adding a new TextField to the group:

        (.addComponent clotouch/group 
           (com.vaadin.ui.TextField. "Enter some text"))

Press the refresh button and the new change should become visible. Notice
how TextField was written out as fully qualified. This is because the namespace
in the REPL is user instead of clotouch. TextField could be imported into that
namespace:

        (import '(com.vaadin.ui TextField))

Or the namespace could be changed to clotouch:

        (ns clotouch)

After which the clotouch namespace does no longer need to be specified to
access group:

        (.addComponent group (TextField. "Enter some text"))

The REPL can be a very powerful tool eg. when figuring out layout
problems, as changes can be made in the REPL and they become immediately
visible.

That's it! What next?
=====================

This has been a short tour of some features of Clojure and TouchKit. Next
step could be to get rid of eclipse completely and to switch Maven to
Leiningen.

The sources for the complete example application can be found
[here](https://github.com/mjvesa/CloTouch.git)

Tobias Bayer has written a good 
[blog post](http://codebrickie.com/blog/2013/02/12/using-vaadin-7-with-clojure/)
about programming Vaadin apps in Clojure with just Leiningen.



