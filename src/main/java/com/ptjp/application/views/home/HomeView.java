package com.ptjp.application.views.home;

import JourneyPlanner.Graph;
import JourneyPlanner.Line;
import JourneyPlanner.RoutingStation;
import JourneyPlanner.Time;
import com.ptjp.application.data.entity.RouteItem;
import com.ptjp.application.data.entity.User;
import com.ptjp.application.data.service.UserService;
import com.ptjp.application.security.AuthenticatedUser;
import com.ptjp.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private Span noRoutesExistMsg;
    private RadioButtonGroup<String> journeyType;
    private TimePicker timePicker;
    private Button goButton;
    private TextField routeTimes;

    // Routing variables
    private final Graph graph;
    
    //Save route variables
    private ConfirmDialog dialog = new ConfirmDialog();
    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private Optional<User> maybeUser;
    private User user;
    private UserService userService;

    public HomeView(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, UserService userService) {
        //initialise variables
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.userService = userService;
        
        // =============================================================================================================
        // Create grid
        grid = new Grid<>(RouteItem.class, false);

        // Add columns
        grid.addColumn(RouteItem::getTrain).setHeader("Train Number").setTextAlign(ColumnTextAlign.CENTER).setWidth("12em").setFlexGrow(0);
        grid.addColumn(RouteItem::getStartTime).setHeader("Departure").setTextAlign(ColumnTextAlign.CENTER).setWidth("8em").setFlexGrow(0);
        grid.addColumn(RouteItem::getStartStation).setHeader("Start").setTextAlign(ColumnTextAlign.CENTER).setWidth("12em").setFlexGrow(0);
        grid.addColumn(RouteItem::getEndStation).setHeader("End").setTextAlign(ColumnTextAlign.CENTER).setWidth("12em").setFlexGrow(0);
        grid.addColumn(RouteItem::getEndTime).setHeader("Arrival").setTextAlign(ColumnTextAlign.CENTER).setWidth("8em").setFlexGrow(0);
        // Add theme
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        // =============================================================================================================
        // Create main input form
        add(getForm());

        // =============================================================================================================
        // Create "No routes exist" label
        noRoutesExistMsg = new Span(new Icon("lumo", "error"), new Span("No routes exist today with the given time"));
        noRoutesExistMsg.getElement().getThemeList().add("badge error");
        noRoutesExistMsg.setVisible(false);
        add(noRoutesExistMsg);

        // =============================================================================================================
        // Create text field for displaying a route's start time, end time and duration
        routeTimes = new TextField();
        routeTimes.setPrefixComponent(new Icon("lumo", "clock"));
        routeTimes.setWidth("57em");
        routeTimes.setVisible(false);
        routeTimes.setReadOnly(true);
        add(routeTimes);

        // =============================================================================================================
        // Create schedule and graph datastructures

        // Read schedule files
        HashMap<String, Line> lines = new HashMap<String, Line>();
        String[] files = new String[]{"North.csv", "South.csv", "Central.csv"};

        for (String fileName : files) {
            // Open file
            Scanner file = null;
            try {
                file = new Scanner(new FileInputStream("src/main/resources/META-INF/resources/data_files/" + fileName));
                file.useDelimiter(";");
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            boolean endOfFileReached = false;
            while (! endOfFileReached) {
                String item;
                // Find "NEW LINE" or "<Line name>"
                do {
                    item = file.next();
                }
                while ((! item.equals("NEW LINE")) && (! isLineName(item)) && (! item.equals("END OF FILE")));

                if (item.equals("END OF FILE"))
                    break;

                LinkedList<String> stations = new LinkedList<String>();
                StringBuilder lineSchedule = new StringBuilder();   // More efficient than string += otherString in a loop. Especially for large strings like this one

                // Repeatedly add file.next() to lineSchedule until the third "END" is reached
                int endCount = 0;
                do {
                    item = file.next();
                    item = item.trim();
                    lineSchedule.append(item); // Add item to lineSchedule string. Equivalent to lineSchedule += item
                    lineSchedule.append(";");

                    if (isStationName(item) && ! (stations.contains(item))) {
                        stations.add(item);
                    }
                    if (item.equals("END")) {
                        endCount++;
                    }
                }
                while ((endCount < 3) && (! item.equals("END OF FILE")));

                if (lineSchedule.length() > 0) {
                    Line line = new Line(stations, lineSchedule.toString());
                    lines.put(line.name(), line);
                }
                endOfFileReached = item.equals("END OF FILE");
            }
        }
        // =============================================================================================================
        // Create graph
        this.graph = new Graph("src/main/resources/META-INF/resources/data_files/CapeTownMap.txt");
        // =============================================================================================================
        JourneyPlanner.Route.setGraph(graph);
        JourneyPlanner.Route.setLines(lines);
    }


    /**
     * Creates the grid to display route information
     * @param route the route object to display infomation about
     * @return the component containing the grid and transfer label
     */
    private Component getGrid(JourneyPlanner.Route route, String start, String end, int dayType) {
        var layout = new VerticalLayout();

        // Get values from route object
        HashMap<String, Line> lines = JourneyPlanner.Route.lines();
        Graph graph = JourneyPlanner.Route.graph();

        String[] stations = route.stations();
        Integer[] trains = route.trainNumbers();
        Time[] times = route.arrivalTimes();
        String[] linesUsed = route.linesUsed();
        int length = stations.length;

        noRoutesExistMsg.setVisible(false);
        grid.setVisible(true);
        if ((stations.length == 0) || (! stations[length - 1].equals(end)) || (! stations[0].equals(start))) {
            noRoutesExistMsg.setVisible(true);
            grid.setVisible(false);
            routeTimes.setVisible(false);
        }
        else {
            routeTimes.setVisible(true);
            routeTimes.setValue(times[0].toString() + " - " + times[length - 1].toString() + "    (" + Time.subtract(times[length - 1], times[0]).inWords() + ")");
        }
        // Reformat trains array if it's from the latest departure algorithm
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
        LinkedList<RouteItem> items = new LinkedList<RouteItem>();
        int i = 0;
        while (i < length - 1) {
            String startStation = stations[i];
            String startTime;
            if (i == 0)
                startTime = times[i].toString(); // get the time from times[]
            else  // Otherwise get the actual departure time for the transfer
                startTime = lines.get(linesUsed[i + 1]).schedule().getTrain(trains[i + 1]).getNextArrivalTimeAt(graph.get(startStation), Time.MIN_TIME, dayType).toString();

            int train = trains[i + 1];
            String endStation;
            String endTime;

            if (i == 0)
                i = -1;  // for j = i + 1 below

            int j;
            for (j = i + 1; j < length; j++) {
                if (j == length - 1 || (j != 0 && !Objects.equals(trains[j], trains[j + 1]))) {  // there's a transfer or we've reached the end
                    endStation = stations[j];
                    endTime = times[j].toString();
                    items.add(new RouteItem(startStation, startTime, endStation, endTime, train));
                    break;
                }
            }
            i = j;
        }

        // Sets the items for the grid
        grid.setItems(items);

        add(grid);

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

        // When the user types in a station, it auto-corrects spelling, checks if it's a valid station and changes it to title case
        start_station_name.setValueChangeMode(ValueChangeMode.ON_BLUR);
        start_station_name.addValueChangeListener(stationTyped -> {
            // Auto-correct common spelling mistakes and convert input into desired form e.g. Mitchells Plain -> Mitchells Pl.
            start_station_name.setValue(titleCase(checkAlternateSpelling(start_station_name.getValue().toUpperCase())));

            // Check if input is a station name
            if (! isStationName(start_station_name.getValue().toUpperCase())) {
                start_station_name.setErrorMessage("Not a station name");
                start_station_name.setInvalid(true);
            }

            goButton.setEnabled(! start_station_name.isInvalid() && ! end_station_name.isInvalid() && ! timePicker.isEmpty());
        });

        end_station_name.setValueChangeMode(ValueChangeMode.ON_BLUR);
        end_station_name.addValueChangeListener(stationTyped -> {
            // Auto-correct common spelling mistakes and convert input into desired form e.g. Mitchells Plain -> Mitchells Pl.
            end_station_name.setValue(titleCase(checkAlternateSpelling(end_station_name.getValue().toUpperCase())));

            // Check if input is a station name
            if (! isStationName(end_station_name.getValue().toUpperCase())) {
                end_station_name.setErrorMessage("Not a station name");
                end_station_name.setInvalid(true);
            }

            goButton.setEnabled(! start_station_name.isInvalid() && ! end_station_name.isInvalid() && ! timePicker.isEmpty());
        });

        // Time input
        timePicker = new TimePicker();
        timePicker.setLabel("Select Time");
        timePicker.setEnabled(false);
        timePicker.addValueChangeListener(timeTyped -> {
            goButton.setEnabled(! start_station_name.isInvalid() && ! end_station_name.isInvalid() && ! timePicker.isEmpty());
        });


        // Radio button for selecting whether the user wants to search by arrival time or by departure time
        journeyType = new RadioButtonGroup<>();
        journeyType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        journeyType.setLabel("Select Journey Type");
        journeyType.setItems("Set Departure Time", "Set Arrival Time");
        journeyType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        journeyType.addValueChangeListener(chosen -> {
            // things to do once an option has been selected
            timePicker.setEnabled(true);  // unfade input field and allow input

            // Set the default time to be the current time if departure was selected
            if (journeyType.getValue().equals("Set Departure Time")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                LocalDateTime now = LocalDateTime.now();
                timePicker.setValue(LocalTime.parse(dtf.format(now)));
            }
            else
                timePicker.clear();
        });
        add(journeyType);


        // Go button
        goButton = new Button("Go");
        goButton.addClickShortcut(Key.ENTER);
        goButton.setEnabled(false);
        goButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        goButton.addClickListener(click -> {
            // Get values from input fields
            String startStationName = start_station_name.getValue().toUpperCase();
            String endStationName = end_station_name.getValue().toUpperCase();

            // Create objects out of the inputs
            RoutingStation startStation = new RoutingStation(this.graph.get(startStationName));
            RoutingStation endStation = new RoutingStation(this.graph.get(endStationName));
            Time startTime = new Time(timePicker.getValue().toString());

            // Determine day type: weekdays = 0, saturdays = 1, sundays = 2
            int dayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // returns 1 for Mondays, 2 for Tuesday, ... 7 for Sunday
            int dayType = 0;
            if (dayOfWeek == 6)
                dayType = 1;
            else if (dayOfWeek == 7)
                dayType = 2;

            // Start routing algorithm
            JourneyPlanner.Route result;
            if (journeyType.getValue().equals("Set Departure Time"))
                result = JourneyPlanner.Route.getRoute_EarliestArrival(startStation, endStation, startTime, dayType);
            else
                result = JourneyPlanner.Route.getRoute_LatestDeparture(startStation, endStation, startTime, dayType);

            add(getGrid(result, startStation.name(), endStation.name(), dayType)); // create the grid showing the route information
        });



        // Configure layout
        layout.add(start_station_name);
        layout.add(end_station_name);
        layout.add(journeyType, timePicker);
        layout.add(goButton);
        
        // verify if user is logged in and add save route button if they are logged in
        maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();

            //saveRoute button
            Button button = new Button("Save Route");
            button.addClickListener(event -> {
                dialog.open();
            });


            dialog.setHeader("Save Route");
            dialog.setText("Do you want to save this route for the future?");

            dialog.setCancelable(true);

            dialog.setRejectable(true);
            dialog.setRejectText("Don't Save");
            dialog.addCancelListener(event ->
                    Notification.show("Not Saved")
            );


            dialog.setConfirmText("Save");
            dialog.addConfirmListener(click -> {
                String start = start_station_name.getValue();
                String end = end_station_name.getValue();
                if(start.equals("") || end.equals("")){
                    Notification.show("Please enter station names");
                }
                
                else {
                    String addedRoute = start + " - " + end + ";";
                    user.setFavouriteRoutes(addedRoute);
                    String allRoutes = user.getFavouriteRoutes();
                    userService.update(user);
                    Notification.show("Saved");
                }
            });
            
            layout.add(button);
        }
        
        return layout;
    }

    /*
     * Some helper methods for string manipulation
     */

    /**
     * Checks if the given string is the name of a station in Cape Town
     * @param text the station name to check in uppercase
     * @return true, if {@code text} is a station name
     */
    private static boolean isStationName(String text) {
        text = text.trim();
        String[] stations = ("ABBOTSDALE;AKASIA PARK;ARTOIS;ATHLONE;AVONDALE;BELHAR;BELLVILLE;BLACKHEATH;BONTEHEUWEL;" +
                "BOTHA;BRACKENFELL;BREE RIVER;CAPE TOWN;CENTURY CITY;CHAVONNES;CHRIS HANI;CLAREMONT;CRAWFORD;" +
                "DAL JOSAFAT;DE GRENDAL;DIEPRIVIER;DU TOIT;EERSTE RIVER;EIKENFONTEIN;ELSIES RIVER;ESPLANADE;" +
                "FALSE BAY;FAURE;FIRGROVE;FISANTEKRAAL;FISH HOEK;GLENCAIRN;GOODWOOD;GOUDA;GOUDINI ROAD;HARFIELD RD;" +
                "HAZENDAL;HEATHFIELD;HEIDEVELD;HERMON;HUGUENOT;KALBASKRAAL;KALK BAY;KAPTEINSKLIP;KENILWORTH;" +
                "KENTEMADE;KHAYELITSHA;KLAPMUTS;KLIPHEUWEL;KOEBERG RD;KOELENHOF;KRAAIFONTEIN;KUILS RIVER;KUYASA;" +
                "LAKESIDE;LANGA;LANSDOWNE;LAVISTOWN;LENTEGEUR;LYNEDOCH;MAITLAND;MALAN;MALMESBURY;MANDALAY;MBEKWENI;" +
                "MELLISH;MELTONROSE;MIKPUNT;MITCHELLS PL.;MONTA VISTA;MOWBRAY;MUIZENBERG;MULDERSVLEI;MUTUAL;NDABENI;" +
                "NETREG;NEWLANDS;NOLUNGILE;NONKQUBELA;NYANGA;OBSERVATORY;OOSTERZEE;OTTERY;PAARDENEILAND;PAARL;PAROW;" +
                "PENTECH;PHILIPPI;PINELANDS;PLUMSTEAD;RETREAT;ROMANS RIVER;RONDEBOSCH;ROSEBANK;SALT RIVER;SAREPTA;" +
                "SIMON'S TOWN;SOETENDAL;SOMERSET WEST;SOUTHFIELD;ST JAMES;STEENBERG;STELLENBOSCH;STEURHOF;STIKLAND;" +
                "STOCK ROAD;STRAND;SUNNY COVE;THORNTON;TULBACHWEG;TYGERBERG;UNIBELL;VAN DER STEL;VASCO;VLOTTENBURG;" +
                "VOËLVLEI;WELLINGTON;WETTON;WINTERVOGAL;WITTEBOME;WOLSELEY;WOLTEMADE;WOODSTOCK;WORCESTER;WYNBERG;YSTERPLAAT")
                .split(";");
        return Arrays.binarySearch(stations, text) >= 0;
    }


    /**
     * Check if the given string is a line name such as "CAPE TOWN - SIMON'S TOWN"
     * @param text the text to check
     * @return true if the text is a line name i.e. contains "-"
     */
    private static boolean isLineName(String text) {
        text = text.trim();
        return text.contains("-");
    }


    /**
     * Correct any alternative spelling e.g. "FISHHOEK" -> "FISH HOEK", "MITCHELLS PLAIN" -> "MITCHELLS PL."
     * @param station the station name to correct
     * @return the corrected form, or the original string if there are no issues
     */
    private static String checkAlternateSpelling(String station) {
        if (! station.equals("SIMON'S TOWN"))
            station = station.replace("'S", "S");  // the only apostrophe-S is in SIMON'S TOWN

        return switch (station) {
            case "FISHHOEK" -> "FISH HOEK";
            case "MITCHELLS PLAIN", "MITCHELL'S PLAIN", "MITCHELL'S PL." -> "MITCHELLS PL.";
            case "BREE RIVER" -> "BREË RIVER";
            case "VOELVLEI" -> "VOËLVLEI";
            case "TULBACH" -> "TULBACHWEG";
            case "ST. JAMES" -> "ST JAMES";
            case "SIMONS TOWN" -> "SIMON'S TOWN";
            case "KOEBERG ROAD", "KOEBERG RD", "KOEBURG ROAD", "KOEBURG RD" -> "KOEBERG RD";
            case "STOCK RD" -> "STOCK ROAD";
            case "GOUDINI RD" -> "GOUDINI ROAD";
            default -> station;
        };
    }


    /**
     * Converts the given string to title case: the first letter of each word is capitalised
     * @param text the string to convert
     * @return the converted string
     */
    private static String titleCase(String text) {
        if (text.equals(""))
            return "";

        text = text.toLowerCase();
        int i = 0;
        do {
            text = text.substring(0, i) + text.substring(i, i + 1).toUpperCase() + text.substring(i + 1);
            i = text.indexOf(" ", i) + 1;
        }
        while (i < text.length() && i != 0);
        return text;
    }


}
