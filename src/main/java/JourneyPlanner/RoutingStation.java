
package JourneyPlanner;

/**
 * A RoutingStation object is a Station object with all the variables needed for Dijkstra's algorithm
 * in the getRoute_EarliestArrival method of the Route class, e.g. cost, prev = previous station visited in the shortest path
 *
 * @author Zac Schmidt, Michael Wade
 * @version 17 Aug 2022, 4:10pm
 */
public class RoutingStation extends Station implements Comparable<RoutingStation> {

    // inherits String name
    private Time cost;
    private RoutingStation prev;
    private Time timeArrivedAt;
    private int prevTrain;  //stores the train number of the train taken to get to this station along the shortest path)
    private boolean popped;

    /**
     * sets everything to default values
     * @param station the corresponding Station object
     */
    public RoutingStation(Station station) {
        super(station);
        // Default values
        this.cost = Time.MAX_TIME;
        this.prev = null;
        this.timeArrivedAt = Time.MAX_TIME;
        this.prevTrain = 0;
        this.popped = false;
    }

    // Getters and setters for everything
    public void setCost(Time cost) {
        this.cost = cost;
    }

    public Time cost() {
        return cost;
    }

    public void setPrev(RoutingStation prev) {
        this.prev = prev;
    }

    public RoutingStation prev() {
        return prev;
    }

    public void setTimeArrivedAt(Time time) {
        this.timeArrivedAt = time;
    }

    public Time timeArrivedAt() {
        return timeArrivedAt;
    }

    public void setPrevTrain(int number) {
        this.prevTrain = number;
    }

    public int prevTrain() {
        return prevTrain;
    }

    public boolean popped() {
        return popped;
    }

    public void setPopped(boolean popped) {
        this.popped = popped;
    }

    /**
     * Compares by name.
     * @param other routing station we are comparing to
     * @return true if they are equal, false otherwise
     */
    public boolean equals(RoutingStation other) {
        return this.name.equals(other.name);
    }

    /**
     * Compares by cost.
     * @param other routing station to compare to
     * @return Positive integer, if this cost is greater than other's cost. Negative integer, if the opposite is true. Zero if this object and other have the same cost.
     */
    public int compareTo(RoutingStation other) {
        return this.cost.compareTo(other.cost());
    }

}
