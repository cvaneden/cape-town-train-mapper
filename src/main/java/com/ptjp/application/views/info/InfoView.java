package com.ptjp.application.views.info;

import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Info")
@Route(value = "info", layout = MainLayout.class)
@AnonymousAllowed
public class InfoView extends VerticalLayout {

    public InfoView() {
        // Information about our project and what we want to achieve as a team
        // Developers, information and goals of project

        add(new H1("Welcome to our journey planner web application to navigate the quickest way " +
                "for you to get to your desired destination"));

        add(new H2("This is a project constructed by UCT 3rd year students with the design and architecture being constructed by Conor Van Eden" +
                " and developers Michael Wade and Zac Schmidt"));



    }
}
