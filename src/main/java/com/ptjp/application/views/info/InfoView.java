package com.ptjp.application.views.info;

import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
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

        add(new H1("Cape Town Train Mapper"));

        add(new Text("This is a Capstone project built by third year UCT students who are currently enrolled in CSC3003S, which is a second semester course in Computer Science at the University of Cape Town."));

        add(new H3("What is our aim?"));
        add(new Text("The aim of this application is to navigate through the city of Cape Town in an easy and efficient way using trains. There are currently not many " +
                "applications that provide an easy interface to interact with. Our application wishes to solve the problem of looking through complicated timetables " +
                "to get to your desired destination."));

        add(new H3("Who are we?"));

        //Conor
        Image img = new Image("images/me.png", "image of Conor");
        System.out.println("Height = "+img.getHeight() +"; Width = " +img.getWidth());
        add(img);
    }

}
