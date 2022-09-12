package com.ptjp.application.views.home;

import com.ptjp.application.data.entity.RouteItem;
import com.ptjp.application.data.entity.Users;
import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
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
import com.vaadin.flow.component.icon.Icon;

import JourneyPlanner.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;


@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {
    private TextField start_station_name = new TextField("Start Station Name");
    private TextField end_station_name = new TextField("End Station Name");
    private ComboBox<Route> startStation = new ComboBox<>("Departure Station");
    private ComboBox<Route> endStation = new ComboBox<>("Destination Station");

    private Grid<RouteItem> grid;
    private Span transferCaption;
    private RadioButtonGroup<String> journeyType;

    // Routing variables
    private final Graph graph = new Graph("src/main/resources/META-INF/resources/textFiles/graph.txt");
    private final Schedule schedule = new Schedule("src/main/resources/META-INF/resources/textFiles/schedule.txt");

    public HomeView() {
        // Create grid
        grid = new Grid<>(RouteItem.class, false);
        grid.setHeight("20em");

        // Add columns
        grid.addColumn(RouteItem::getStation).setHeader("Station").setTextAlign(ColumnTextAlign.CENTER).setWidth("8em").setFlexGrow(0);
        grid.addColumn(RouteItem::getTime).setHeader("Time").setTextAlign(ColumnTextAlign.CENTER).setWidth("8em").setFlexGrow(0);
        grid.addColumn(RouteItem::getTrain).setHeader("Train Number").setTextAlign(ColumnTextAlign.CENTER).setWidth("8em").setFlexGrow(0);
        // Add theme
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        // Highlight rows that have transfers
        grid.setClassNameGenerator(item -> {
            if (item.getTransfer())
                return "transfer";   // "transfer" refers to the entry in frontend/themes/capetoentrainmapper/components/vaadin-grid.css
            return null;
        });

        // Create transfers label
        Icon icon = VaadinIcon.TRAIN.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        transferCaption = new Span(icon, new Span("Transfers"));
        transferCaption.getElement().getThemeList().add("badge");

        // Create main input form
        add(getForm());
    }

    /**
     * Creates the grid to display route information
     * @param route the route object to display infomation about
     * @return the component containing the grid and transfer label
     */
    private Component getGrid(JourneyPlanner.Route route) {
        var layout = new HorizontalLayout();

        // Get values from route object
        String[] stations = route.stations();
        Integer[] trains = route.trainNumbers();
        Time[] times = route.arrivalTimes();
        int length = stations.length;

        // Reformat trains array if it's from latest departure algorithm
        // [3, 3, 3, 4, 0] --> [0, 3, 3, 3, 4]
        if (journeyType.getValue().equals("Set Arrival Time")) {
            for (int i = length - 1; i > 0; i--) {
                // Swap temp[i] and temp[i - 1]
                int temp = trains[i];
                trains[i] = trains[i - 1];
                trains[i - 1] = temp;
            }
        }
        // Create an array of RouteItems which will be the rows in the grid
        RouteItem[] items = new RouteItem[length];
        boolean transfer;
        boolean atLeastOneTransfer = false;  // if there is at least one transfer in this route

        for (int i = 0; i < length - 1; i++) {
            transfer = (i != 0) && (trains[i + 1] != trains[i]);  // if the train numbers change, then there's a transfer
            items[i] = new RouteItem(stations[i], times[i].toString(), trains[i + 1], transfer);

            if (! atLeastOneTransfer && transfer)
                atLeastOneTransfer = true;
        }
        transfer = trains[length - 2] != trains[length - 1]; // if the train numbers change, then there's a transfer
        items[length - 1] = new RouteItem(stations[length - 1], times[length - 1].toString(), trains[length - 1], transfer);

        if (! atLeastOneTransfer && transfer)
            atLeastOneTransfer = true;

        // Sets the items for the grid
        grid.setItems(items);

        add(grid);

        // Set transferCaption to be visible only when there is at least one transfer in the route
        layout.add(transferCaption);
        transferCaption.setVisible(false);
        if (atLeastOneTransfer)
            transferCaption.setVisible(true);

        // Configure layout
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(false);
        layout.getThemeList().add("spacing-xs");
        return layout;
    }

    /**
     * Creates the main input form for this view. Includes input fields for start and end stations,
     * the arrival/departure times and the date.
     * @return the component containing all the input fields
     */
    private Component getForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);

        // Date input
        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Select Date");

        // Time input
        TimePicker timePicker = new TimePicker();
        timePicker.setLabel("Select Time");
        timePicker.setEnabled(false);

        // Radio button for selecting whether the user wants to search by arrival time or by departure time
        journeyType = new RadioButtonGroup<>();
        journeyType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        journeyType.setLabel("Select Journey Type");
        journeyType.setItems("Set Departure Time", "Set Arrival Time");
        journeyType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        journeyType.addValueChangeListener(chosen -> { timePicker.setEnabled(true); });
        add(journeyType);

        // Go button
        var addButton = new Button("Go");
        addButton.addClickShortcut(Key.ENTER);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(click -> {
            // Get values from input fields and create objects
            RoutingStation startStation = new RoutingStation(this.graph.get(start_station_name.getValue()));
            RoutingStation endStation = new RoutingStation(this.graph.get(end_station_name.getValue()));
            Time startTime = new Time(timePicker.getValue().toString());

            // Start routing algorithm
            JourneyPlanner.Route result;
            if (journeyType.getValue().equals("Set Departure Time"))
                result = JourneyPlanner.Route.getRoute_EarliestArrival(startStation, endStation, startTime);
            else
                result = JourneyPlanner.Route.getRoute_LatestDeparture(startStation, endStation, startTime);

            add(getGrid(result)); // create the grid showing the route information
        });

        // Configure layout
        layout.add(start_station_name);
        layout.add(end_station_name);
        layout.add(journeyType, timePicker);
        layout.add(datePicker, addButton);
        return layout;
    }

}
