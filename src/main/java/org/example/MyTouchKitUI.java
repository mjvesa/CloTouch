package org.example;

import java.io.IOException;

import clojure.lang.RT;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * The UI's "main" class
 */
@Widgetset("org.example.gwt.AppWidgetSet")
public class MyTouchKitUI extends UI {
	@Override
	protected void init(VaadinRequest request) {
		try {
			RT.loadResourceScript("clotouch.clj");
			RT.var("clotouch", "main").invoke(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}