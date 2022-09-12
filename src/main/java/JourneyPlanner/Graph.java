
package JourneyPlanner;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Arrays;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * An unweighted, undirected graph data structure whose nodes are Station objects
 *
 * @author Michael Wade
 * @version 16 Aug 2022, 8:30pm 
 */
public class Graph {
    private Hashtable<String, Station> graph;

    /**
     * Hardcode the sample graph
     */
    public Graph() {
        this.graph = new Hashtable<String, Station>();
        this.graph.put("A", new Station("A", new LinkedList<String>(Arrays.asList("B, F, G".split(", ")))));
        this.graph.put("B", new Station("B", new LinkedList<String>(Arrays.asList("A, F, C".split(", ")))));
        this.graph.put("C", new Station("C", new LinkedList<String>(Arrays.asList("B, F, D".split(", ")))));
        this.graph.put("D", new Station("D", new LinkedList<String>(Arrays.asList("C, E".split(", ")))));
        this.graph.put("E", new Station("E", new LinkedList<String>(Arrays.asList("D, F".split(", ")))));
        this.graph.put("F", new Station("F", new LinkedList<String>(Arrays.asList("A, B, C, E".split(", ")))));
        this.graph.put("G", new Station("G", new LinkedList<String>(Arrays.asList("A, H".split(", ")))));
        this.graph.put("H", new Station("H", new LinkedList<String>(Arrays.asList("G".split(", ")))));
    }

    /**
     * Fill up a graph of stations from a file. With typo detection.
     *
     * File format. The first line should specify the number of nodes. Thereafter, each line should have this format:
     * <Station name>: <Adjacent station name 1>, <Adjacent station name 2>, ... <Adjacent station name n>
     *
     * Note that if the entry for station A says that it is adjacent to station B, then the entry for station B should
     * say that it is adjacent to station A as well (this is not checked automatically).
     * @param fileName the file which contains the graph information
     */
    public Graph(String fileName) {
        // Open file
        Scanner file = null;
        try {
            file = new Scanner(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get size of graph from file
        int size = file.nextInt();
        file.nextLine();
        this.graph = new Hashtable<String, Station>(size);

        // Get stations from file
        while (file.hasNextLine()) {
            String stationName = file.next().toUpperCase();
            stationName = stationName.substring(0, stationName.length() - 1);   // remove the ":" at the end
            String[] adjacentStations = file.nextLine().split(", ");
            adjacentStations[0] = adjacentStations[0].substring(1);   // remove the leading " "

            // Typo Detection
            if (hasTypo(stationName)) {
                System.out.println("Warning: \""  + stationName + "\" may have a typo. Found in station names. ");
                System.exit(0);
            }
            for (int i = 0; i < adjacentStations.length; i++) {
                adjacentStations[i] = adjacentStations[i].toUpperCase();
                if (hasTypo(adjacentStations[i])) {
                    System.out.println("Warning: \"" + adjacentStations[i] + "\" may have a typo. Found in stations adjacent to " + stationName);
                    System.exit(0);
                }
            }

            // Create Station object and add it to the graph
            this.graph.put(stationName, new Station(stationName, new LinkedList<String>(Arrays.asList(adjacentStations))));
        }
    }


    /**
     * Checks if the given string doesn't match one of the station names in Cape Town. This then
     * is probably a typo in the name.
     * @param name the name to check for typos
     * @return True, if typo is found. False, otherwise
     */
    private static boolean hasTypo(String name) {
        String[] stationNames = "CAPE_TOWN WOODSTOCK SALT_RIVER KOEBERG_RD MAITLAND NDABENI PINELANDS ESPLANADE PAARDENEILAND YSTERPLAAT MUTUAL LANGA BONTEHEUWEL NETREG HEIDEVELD NYANGA PHILIPPI LENTEGEUR MITCHELLS_PL. KAPTEINSKLIP STOCK_ROAD MANDALAY NOLUNGILE NONKQUBELA KHAYELITSHA KUYASA CHRIS_HANI LAVISTOWN BELHAR UNIBELL PENTECH SAREPTA BELLVILLE A B C D E F G H I".split(" ");
        for (String stationName : stationNames) {
            if (name.equalsIgnoreCase(stationName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get a station from the graph
     * @param stationName the name of the station to get
     * @return the station in the graph with this name
     */
    public Station get(String stationName) {
        return this.graph.get(stationName);
    }
}
