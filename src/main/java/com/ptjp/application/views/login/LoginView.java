package com.ptjp.application.views.login;

import com.ptjp.application.security.AuthenticatedUser;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction("login");

        LoginI18n loginForm = LoginI18n.createDefault();
        loginForm.setHeader(new LoginI18n.Header());
        loginForm.getHeader().setTitle("Cape Town Train Mapper");
        loginForm.getHeader().setDescription("Login using credentials. If new, click the sign-up button.");
        loginForm.setAdditionalInformation(null);
        setI18n(loginForm);

        setForgotPasswordButtonVisible(false);
        setOpened(true);


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("home");
        }
        else{
            setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
        }

    }
}
