package com.ptjp.application.views.map;

import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@AnonymousAllowed
public class MapView extends VerticalLayout {

    public MapView() {
        setSpacing(false);

        Image img = new Image("images/cape-town-rail-map.png", "map of cape town city trains");

        img.setWidthFull();
        add(img);


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

}
