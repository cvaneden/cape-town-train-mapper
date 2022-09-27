
package JourneyPlanner;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A Line object is a sequence of adjacent stations on the map which trains pass daily
 * e.g. the "Cape Town - Simon's Town" line contains Cape Town, Woodstock, Salt River, ..., Simon's Town.
 * Note that order matters, so a line object "Cape Town - Simon's Town" is different from its reverse "Simon's Town - Cape Town".
 * Line objects are used to group stations together to allow for faster searching in the routing algorithm.
 * @author Michael Wade
 * @see Station
 * @see Schedule
 */
public class Line {
    /**
     * Name of the line. Has the form "[first station] - [last station]"
     */
    private String name;

    /**
     * The stations in this line. This is stored as a hashtable to improve lookup speed. Keys are the station names,
     * and values are the order. e.g. Cape Town, Woodstock, ... Simon's Town becomes {"Cape Town" : 0, "Woodstock" : 1, ..., "Simon's Town" : 27}.
     * This means we can check if a station S precedes station T on the line very quickly
     */
    private HashMap<String, Integer> stations;

    /**
     * The full schedule for this line including weekdays and weekends
     */
    private Schedule schedule;

    /**
     * Creates a new Line object given an array of stations it contains and a semicolon-delimitered text containing all
     * the train numbers and arrival times for the schedule. See the Schedule constructor for the format of this text.
     * @param stations array of station names
     * @param csvText formatted text
     * @throws NullPointerException if csvText is incorrectly formatted
     */
    public Line(LinkedList<String> stations, String csvText) {
        this(stations, new Schedule(csvText));
    }


    /**
     * Creates a new Line object given the array of stations it contains and the schedule for this line.
     * @param stations the list of stations it contains in the order that trains pass them
     * @param schedule the schedule for this line
     */
    public Line(LinkedList<String> stations, Schedule schedule) {
        this.name = stations.getFirst() + " - " + stations.get(stations.size()/2) + " - " + stations.getLast();
        this.schedule = schedule;
        this.stations = new HashMap<String, Integer>();

        for (int j = 0; j < stations.size(); j++) {
            this.stations.put(stations.get(j), j);
        }
    }


    /**
     * Returns this line's schedule
     * @return the schedule object
     */
    public Schedule schedule() {
        return this.schedule;
    }


    /**
     * Get the name of this line
     * @return the line name
     */
    public String name() {
        return this.name;
    }


    /**
     * Checks if the given station is in this line
     * @param station the station to look for
     * @return true if the station is on this line, false otherwise
     * @throws NullPointerException if station is null
     */
    public boolean contains(Station station) {
        return this.stations.containsKey(station.name());
    }


    /**
     * Checks if both stations are on this line and if station1 appears before station2, or if only one of them is on the line.
     * @param station1 the first station
     * @param station2 the second station
     * @return If both stations are on the line, and station1 appears before station2 on the line then it returns 3. If station2 appears after station1, then it returns -1. Returns 2, if only station2 is on the line. 1, if only station1 is on the line and 0 if neither of the stations are on this line
     * @throws NullPointerException if either station1 or station2 is null
     */
    public int contains(Station station1, Station station2) {
        int result = 0;
        boolean contains1 = this.contains(station1);
        boolean contains2 = this.contains(station2);

        if (contains1)
            result = 1;
        if (contains2)
            result = 2;

        if (contains1 && contains2) {
            if (this.stations.get(station1.name()) < this.stations.get(station2.name()))
                result = 3;
            else
                result = -1;
        }
        return result;
    }
}
