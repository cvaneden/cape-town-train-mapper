
package JourneyPlanner;

/**
 * A RoutingStation object is a Station object with all the variables needed for Dijkstra's algorithm
 * in the {@code getRoute} methods of the {@code Route} class, e.g. cost, prev = previous station visited in the shortest path
 *
 * @author Zac Schmidt, Michael Wade
 * @see Station
 * @see Route
 */
public class RoutingStation extends Station implements Comparable<RoutingStation> {

    // inherits String name
    /**
     * The amount of time in hours and minutes to travel from starting node to this node
     */
    private Time cost;

    /**
     * Pointer to the previous RoutingStation along the shortest path
     */
    private RoutingStation prev;

    /**
     * Pointer to the next RoutingStation on the shortest path
     */
    private RoutingStation next;

    /**
     * The time a train arrived at this station, where the train is the one taken on the shortest path
     */
    private Time timeArrivedAt;

    /**
     * The train number of the train taken to get to this station on the shortest path
     */
    private int prevTrain;

    /**
     * The line that prevTrain travels on
     */
    private String prevLine;

    /**
     * Indicated if this station has been popped from the priority queue used in Dijkstra's Algorithm
     */
    private boolean popped;


    /**
     * Creates a RoutingStation object extended from a non-null Station object. Name and adjacentStations (from Station) are
     * given with pass-by-reference. The variables of RoutingStation are initialised to the default values
     * {@code cost = Time.MAX_TIME, prev = null, timeArrivedAt = Time.MAX_TIME, prevTrain = 0, prevLine = null, popped = false}
     * @param station the corresponding Station object
     * @throws NullPointerException if station is null
     */
    public RoutingStation(Station station) {
        super(station);
        // Default values
        this.cost = Time.MAX_TIME;
        this.prev = null;
        this.next = null;
        this.timeArrivedAt = Time.MAX_TIME;
        this.prevTrain = 0;
        this.prevLine = null;
        this.popped = false;
    }

    // Getters and setters for everything

    /**
     * Set the cost of this RoutingStation
     * @param cost
     */
    public void setCost(Time cost) {
        this.cost = cost;
    }


    /**
     * Get the cost of this RoutingStation
     * @return the cost
     */
    public Time cost() {
        return cost;
    }


    /**
     * Set the prev RoutingStation of this RoutingStation
     * @param prev
     */
    public void setPrev(RoutingStation prev) {
        this.prev = prev;
    }


    /**
     * Get the prev RoutingStation of this RoutingStation
     * @return the prev RoutingStation
     */
    public RoutingStation prev() {
        return prev;
    }


    /**
     * Set the next RoutingStation of this RoutingStation
     * @param next
     */
    public void setNext(RoutingStation next) {
        this.next = next;
    }


    /**
     * Get the next RoutingStation of this RoutingStation
     * @return the next RoutingStation
     */
    public RoutingStation next() {
        return next;
    }


    /**
     * Set the timeArrivedAt of this RoutingStation
     * @param time
     */
    public void setTimeArrivedAt(Time time) {
        this.timeArrivedAt = time;
    }


    /**
     * Get the timeArrivedAt of this RoutingStation
     * @return the timeArrivedAt as a time object
     */
    public Time timeArrivedAt() {
        return timeArrivedAt;
    }


    /**
     * Set the prevTrain of this RoutingStation
     * @param number
     */
    public void setPrevTrain(int number) {
        this.prevTrain = number;
    }


    /**
     * Get the prevTrain of this RoutingStation
     * @return the prevTrain, its train number
     */
    public int prevTrain() {
        return prevTrain;
    }


    /**
     * Get the popped state of this RoutingStation
     * @return the value of popped
     */
    public boolean popped() {
        return popped;
    }


    /**
     * Set the popped state of this RoutingStation
     * @param popped
     */
    public void setPopped(boolean popped) {
        this.popped = popped;
    }


    /**
     * Get the prevLine of this RoutingStation
     * @return the prevLine
     */
    public String prevLine() {
        return prevLine;
    }


    /**
     * Set the prevLine of this RoutingStation
     * @param prevLine
     */
    public void setPrevLine(String prevLine) {
        this.prevLine = prevLine;
    }


    /**
     * Compares by name.
     * @param other routing station we are comparing to
     * @return true if they are equal, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean equals(RoutingStation other) {
        return this.name.equals(other.name);
    }


    /**
     * Compares by cost.
     * @param other routing station to compare to
     * @return Positive integer, if this cost is greater than other's cost. Negative integer, if the opposite is true. Zero if this object and other have the same cost
     * @throws NullPointerException if other is null
     */
    public int compareTo(RoutingStation other) {
        return this.cost.compareTo(other.cost());
    }

}
