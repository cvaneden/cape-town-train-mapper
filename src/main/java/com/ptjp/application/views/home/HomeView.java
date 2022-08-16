package com.ptjp.application.views.home;

import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {
    private TextField start_station_name = new TextField("Start Station Name");
    private TextField end_station_name = new TextField("End Station Name");

    private ComboBox<Route> startStation = new ComboBox<>("Departure Station");

    private ComboBox<Route> endStation = new ComboBox<>("Destination Station");


    public HomeView() {

         add(getForm()); // left out: grid

    }
        private Component getForm() {
            var layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.BASELINE);

            // getting date and times
            DatePicker datePicker = new DatePicker();
            datePicker.setLabel("Select Date");

            TimePicker timePicker = new TimePicker();
            timePicker.setLabel("Select Time");

            // getting arrival or depature method
            CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
            checkboxGroup.setLabel("Select Journey Type");
            checkboxGroup.setItems("Depart", "Arrival");
            checkboxGroup.select("Depart");
            checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);


            var addButton = new Button("Go");
            addButton.addClickShortcut(Key.ENTER);
            addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            layout.add(start_station_name);
            layout.add(end_station_name);
            layout.add(timePicker, datePicker);
            layout.add(checkboxGroup, addButton);



            return layout;
        }

}
