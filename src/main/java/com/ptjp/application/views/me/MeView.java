package com.ptjp.application.views.me;

import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.service.UserService;
import com.ptjp.application.security.AuthenticatedUser;
import com.ptjp.application.views.MainLayout;
import com.ptjp.application.views.register.RegisterView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Optional;

@PageTitle("Me")
@Route(value = "me", layout = MainLayout.class)
@RolesAllowed("USER")
public class MeView extends VerticalLayout {
    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    Optional<User> maybeUser;
    User user;
    public MeView(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        add(profile());
        add(savedRoutes());
    }

    private Component profile(){
        var layout = new VerticalLayout();

        maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();

            Span name = new Span("Name: "+user.getName());
            Span username = new Span("Username: "+user.getUsername());

            VerticalLayout content = new VerticalLayout(name, username);
            content.setSpacing(false);
            content.setPadding(false);

            Details details = new Details("Profile", content);
            details.setOpened(true);
            layout.add(details);
        }

        return layout;
    }

    private Component savedRoutes(){
        var layout = new VerticalLayout();

        maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();

            if (user.hasRoutes()) {
                // creating span objects in order to display to screen
                String[] routes = user.getFavouriteRoutes_List();
                ArrayList<Span> routeList = new ArrayList<>();
                for (int i=0; i<routes.length; i++) {
                    Span r = new Span((i+1)+": "+routes[i]);
                    routeList.add(r);
                }

                // adding to layout
                VerticalLayout content = new VerticalLayout();
                for (int i=0; i<routeList.size(); i++) {
                    content.add(routeList.get(i));
                }
                content.setSpacing(false);
                content.setPadding(false);

                Details details = new Details("Saved Routes", content);
                details.setOpened(true);
                layout.add(details);
            }

        }

        return layout;
    }


}
