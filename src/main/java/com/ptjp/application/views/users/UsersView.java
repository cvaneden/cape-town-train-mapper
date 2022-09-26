package com.ptjp.application.views.users;

import com.ptjp.application.data.Role;
import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.service.UserService;
import com.ptjp.application.security.SecurityConfiguration;
import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("Users")
@Route(value = "Users/:usersID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends Div implements BeforeEnterObserver {

    private final String USER_ID = "userID";
    private final String USERS_EDIT_ROUTE_TEMPLATE = "Users/%s/edit";

    private Grid<User> grid = new Grid<>(User.class, false);

    private TextField name;
    private TextField username;

    private TextField role;
    private TextField password;
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<User> binder;

    private User users;

    private final UserService usersService;
    private RadioButtonGroup<String> roleType;

    @Autowired
    public UsersView(UserService userService) {
        this.usersService = userService;
        addClassNames("users-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("favouriteRoutes").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        grid.setItems(query -> usersService.list(
                PageRequest.of(query.getPage(),
                query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        // Configure Form
        binder = new BeanValidationBinder<>(User.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (name.getValue().trim().isEmpty() || username.getValue().trim().isEmpty() || password.getValue().trim().isEmpty()) {
                    Notification.show("Missing Information, please try again");
                }
                else if (roleType.getValue().equals("User")){
                    PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
                    users = new User(username.getValue(), name.getValue(), passwordEncoder.encode(password.getValue()), Role.USER);
                    binder.writeBean(this.users);
                    usersService.update(this.users);
                    clearForm();
                    refreshGrid();
                    Notification.show("Users details stored.");
                    UI.getCurrent().navigate(UsersView.class);
                }
                else{
                    PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
                    users = new User(username.getValue(), name.getValue(), passwordEncoder.encode(password.getValue()), Role.ADMIN);
                    binder.writeBean(this.users);
                    usersService.update(this.users);
                    clearForm();
                    refreshGrid();
                    Notification.show("Users details stored.");
                    UI.getCurrent().navigate(UsersView.class);
                }
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the users details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> usersId = event.getRouteParameters().get(USER_ID).map(UUID::fromString);
        if (usersId.isPresent()) {
            Optional<User> usersFromBackend = usersService.get(usersId.get());
            if (usersFromBackend.isPresent()) {
                populateForm(usersFromBackend.get());
            } else {
                Notification.show(String.format("The requested users was not found, ID = %s", usersId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(UsersView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        username = new TextField("Username");
        password = new TextField("Password");

        roleType = new RadioButtonGroup<>();
        roleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        roleType.setLabel("Role");
        roleType.setItems("User", "Admin");
        roleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        Component[] fields = new Component[]{name, username, password, roleType};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(User value) {
        this.users = value;
        binder.readBean(this.users);

    }
}
