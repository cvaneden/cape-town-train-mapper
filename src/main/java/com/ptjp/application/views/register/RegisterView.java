package com.ptjp.application.views.register;


import com.ptjp.application.data.Role;
import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.service.UserService;
import com.ptjp.application.security.SecurityConfiguration;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;


@PageTitle("Register")
@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends Composite {

    private final UserService userService;

    TextField name = new TextField("Name & Surname");
    TextField username = new TextField("Username");
    // TextField emailAddress = new TextField("Email Address");

    PasswordField password1 = new PasswordField("Password");
    PasswordField password2 = new PasswordField("Confirm Password");

    public RegisterView(UserService userService){
        this.userService = userService;
    }

    @Override
    protected Component initContent() {
        Anchor loginLink = new Anchor("login", "Sign in with new Account, Dont forget details!");
        Anchor homeLink = new Anchor("home", "Back to Home");
        return new VerticalLayout(
                //homeLink,
                new H2("Sign-up"),
                loginLink,
                name,
                username,
                password1,
                password2,
                new Button("Create Account", event -> register(
                        name.getValue(),
                        username.getValue(),
                        password1.getValue(),
                        password2.getValue()
                ))
        );
    }



    private void register(String name, String username, String password1, String password2) {
        if(name.trim().isEmpty()){
            Notification.show("Enter your name");
        }
        else if(username.trim().isEmpty()){
            Notification.show("Enter a username");
        }
        else if(password1.isEmpty()){
            Notification.show("Enter a password");
        }
        else if(!password1.equals(password2)){
            Notification.show("Passwords don't match");
        }
        else if(userService.hasUsername(username)){
            Notification.show("A user exists with that username, pick another one");
        }
        else if (password1.length()<8) {
            Notification.show("Password is too short. Must be 8 characters minimum");
        } else {
            PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
            User user = new User(username, name, passwordEncoder.encode(password1), Role.USER);
            userService.update(user);
            Notification.show("Registration Successful - navigate back to home or Login Page");
        }
    }
}
