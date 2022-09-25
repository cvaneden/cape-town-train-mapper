
package JourneyPlanner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * An unweighted, undirected graph data structure whose nodes are Station objects
 *
 * @author Michael Wade
 */
public class Graph {
    /**
     * The collection of nodes in the graph. Keys = station names, values = station objects
     */
    private HashMap<String, Station> graph;


    /**
     * Fill up a graph of stations from a file. With typo detection.
     *
     * <p>File format. The first line should specify the number of nodes. Thereafter, each line should have this format:
     * <p>[Station name]: [Adjacent station name 1], [Adjacent station name 2], ... [Adjacent station name n]
     *
     * <p>Note that if the entry for station A says that it is adjacent to station B, then the entry for station B should
     * say that it is adjacent to station A as well (this is not checked automatically).
     * @param fileName the file which contains the graph information
     * @throws java.util.NoSuchElementException if the file is formatted incorrectly
     * @throws java.util.InputMismatchException if the first line doesn't specify the number of nodes as an integer
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
        file.useDelimiter(":");
        this.graph = new HashMap<String, Station>(size);

        // Get stations from file
        while (file.hasNextLine()) {
            String stationName = file.next();
            String[] adjacentStations = file.nextLine().trim().split(", ");
            adjacentStations[0] = adjacentStations[0].substring(2);   // remove the leading ": "

            // Create Station object and add it to the graph
            this.graph.put(stationName, new Station(stationName, new LinkedList<String>(Arrays.asList(adjacentStations))));
        }
    }


    /**
     * Checks if the given string doesn't match one of the station names in Cape Town. This then
     * is probably a typo in the name
     * @param name the name to check for typos
     * @return True, if typo is found. False, otherwise
     * @throws IllegalArgumentException if name is null
     * @deprecated
     */
    public static boolean hasTypo(String name) {
        if (name == null)
            throw new IllegalArgumentException("name can't be null");
        String stationNames = "ATHLONE," +
                              "BELHAR," +
                              "BELLVILLE," +
                              "BLACKHEATH," +
                              "BONTEHEUWEL," +
                              "BRACKENFELL," +
                              "CAPE TOWN," +
                              "CHRIS HANI," +
                              "CLAREMONT," +
                              "CRAWFORD," +
                              "DAL JOSAFAT," +
                              "DIEPRIVIER," +
                              "DU TOIT," +
                              "EERSTE RIVER," +
                              "EIKENFONTEIN," +
                              "ELSIES RIVER," +
                              "ESPLANADE," +
                              "FALSE BAY," +
                              "FAURE," +
                              "FIRGROVE," +
                              "FISH HOEK," +
                              "GLENCAIRN," +
                              "GOODWOOD," +
                              "HARFIELD RD," +
                              "HAZENDAL," +
                              "HEATHFIELD," +
                              "HEIDEVELD," +
                              "HUGUENOT," +
                              "KALK BAY," +
                              "KAPTEINSKLIP," +
                              "KENILWORTH," +
                              "KHAYELITSHA," +
                              "KLAPMUTS," +
                              "KOEBERG RD," +
                              "KOELENHOF," +
                              "KRAAIFONTEIN," +
                              "KUILS RIVER," +
                              "KUYASA," +
                              "LAKESIDE," +
                              "LANGA," +
                              "LANSDOWNE," +
                              "LAVISTOWN," +
                              "LENTEGEUR," +
                              "LYNEDOCH," +
                              "MAITLAND," +
                              "MANDALAY," +
                              "MBEKWENI," +
                              "MELTONROSE," +
                              "MITCHELLS PL.," +
                              "MOWBRAY," +
                              "MUIZENBERG," +
                              "MULDERSVLEI," +
                              "MUTUAL," +
                              "NDABENI," +
                              "NETREG," +
                              "NEWLANDS," +
                              "NOLUNGILE," +
                              "NONKQUBELA," +
                              "NYANGA," +
                              "OBSERVATORY," +
                              "OTTERY," +
                              "PAARDENEILAND," +
                              "PAARL," +
                              "PAROW," +
                              "PENTECH," +
                              "PHILIPPI," +
                              "PINELANDS," +
                              "PLUMSTEAD," +
                              "RETREAT," +
                              "RONDEBOSCH," +
                              "ROSEBANK," +
                              "SALT RIVER," +
                              "SAREPTA," +
                              "SIMON'S TOWN," +
                              "SOMERSET WEST," +
                              "SOUTHFIELD," +
                              "STEENBERG," +
                              "STELLENBOSCH," +
                              "STEURHOF," +
                              "STIKLAND," +
                              "STOCK ROAD," +
                              "STRAND," +
                              "ST JAMES," +
                              "SUNNY COVE," +
                              "THORNTON," +
                              "TYGERBERG," +
                              "UNIBELL," +
                              "VAN DER STEL," +
                              "VASCO," +
                              "VLOTTENBURG," +
                              "WELLINGTON," +
                              "WETTON," +
                              "WITTEBOME," +
                              "WOLTEMADE," +
                              "WOODSTOCK," +
                              "WYNBERG," +
                              "YSTERPLAAT";

        return Arrays.binarySearch(stationNames.split(","), name) < 0; // binary search for the name: if it's found return false (no typo), else return true
    }


    /**
     * Get a station from the graph
     * @param stationName the name of the station to get
     * @return the station in the graph with this name
     * @throws NullPointerException if stationName is null
     */
    public Station get(String stationName) {
        return this.graph.get(stationName);
    }
}
