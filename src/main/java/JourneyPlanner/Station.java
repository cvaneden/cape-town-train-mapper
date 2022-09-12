
package JourneyPlanner;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * A Station object acts as a node in the graph
 *
 * @author Michael Wade
 * @version 17 Aug 2022, 4:09pm
 */
public class Station {
    protected String name;
    protected LinkedList<String> adjacentStations;


    /**
     * Creates a new Station object
     * @param name the unique name of the station
     * @param adjacentStations A hashtable of the adjacent stations in the graph. Keys are the station names, values are the stations
     */
    public Station(String name, LinkedList<String> adjacentStations) {
        this.name = name;
        this.adjacentStations = adjacentStations;
    }

    /**
     * Creates a new Station object
     * @param name the unique name of the station
     */
    public Station(String name) {
        this.name = name;
        this.adjacentStations = new LinkedList<String>();
    }

    /**
     * Creates a shallow copy of the given Station object.
     * @param station the station to copy
     */
    protected Station(Station station) {
        this.name = station.name;
        this.adjacentStations = station.adjacentStations;   // pass by reference
    }

    /**
     * Returns an array of the names of the stations which are adjacent to this station
     * @return array of adjacent station names
     */
    public String[] adjacentStations() {
        return this.adjacentStations.toArray(new String[this.adjacentStations.size()]);

    }


    public boolean isAdjacentTo(Station other) {
        return this.adjacentStations.contains(other.name());
    }


    /**
     * Set a station as being adjacent to this one. Note that addAdjacentStation is automatically also called
     * on the parameter station to add the calling station to its list of adjacent stations. That is
     * S.addAdjacentStation(T) also calls T.addAdjacentStation(S).
     * @param station the station to add as adjacent. Not null.
     * @return true, if the station was added. false, if the stations are already adjacent
     */
    boolean addAdjacentStation(Station station) {   // no access modifier ==> accessed within package, but not by subclasses
        boolean result = false;

        if (! this.isAdjacentTo(station)) {
            station.addAdjacentStation(this);
            result = true;
        }
        if (! station.isAdjacentTo(this)) {
            this.adjacentStations.add(station.name());
            result = true;
        }
        return result;
    }


    /**
     * Remove a station from being adjacent to this one. This can be used to remove a railway track from the graph so
     * that it is avoided during routing. Note that S.removeAdjacentStation(T) also calls T.removeAdjacentStation(S). So
     * this method essentially removes the edge ST from the graph.
     * @param stationName the adjacent station to remove. Not null.
     * @return true, if the station was removed. false, if the stations aren't adjacent
     */
    boolean removeAdjacentStation(String stationName) {   // no access modifier ==> accessed within package, but not by subclasses
        if (this.adjacentStations.contains(stationName)) {
            this.adjacentStations.remove(stationName);
            return true;
        }
        return false;
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return this.name + ", " + Arrays.toString(this.adjacentStations());
    }
}
