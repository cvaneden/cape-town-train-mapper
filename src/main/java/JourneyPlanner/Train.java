
package JourneyPlanner;

import java.util.HashMap;

/**
 * Represents a train which moves through a map of stations according to a fixed schedule.
 *
 * @author Michael Wade
 * @see Station
 */
public class Train {
    /**
     * The train number (unique)
     */
    private int number;

    /**
     * Collection of stations this train passes paired with the arrival times on weekdays (index 0), saturdays (index 1), and sundays (index 2)
     */
    private HashMap<String, Time[]> schedule;


    /**
     * Creates a Train object with the given train number and an empty schedule. Can use addStop(station) to add
     * stations to this trains schedule.
     * @param number the train number
     */
    public Train(int number) {
        this.number = number;
        this.schedule = new HashMap<String, Time[]>();
    }


    /**
     * Create a new Train object
     * @param number: the unique train number
     * @param schedule: the schedule for this train
     */
    public Train(int number, HashMap<String, Time[]> schedule) {
        this.number = number;
        this.schedule = schedule;
    }


    /**
     * Get the train number for this train
     */
    public int number() {
        return number;
    }


    /**
     * Gets the time that this train passes stationName next
     *
     * @param station the target station
     * @param currentTime only arrival times after this time are considered
     * @param dayType an integer representing the type of day: weekday, saturday, sunday. Use of the constants in {@code Schedule}
     * @return the next time (after currentTime) that this train passes stationName. Or returns Time.MAX_TIME if the train doesn't pass this station
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if dayType is an integer that is neither 0, 1, nor 2
     */
    public Time getNextArrivalTimeAt(Station station, Time currentTime, int dayType) {
        if (! (0 <= dayType && dayType < 3))
            throw new IllegalArgumentException("dayType must be either 0, 1, or 2");

        Time[] times = schedule.get(station.name());
        Time arrivalTime = null;
        if (times != null)
            arrivalTime = times[dayType];

        if (arrivalTime != null) {
            if (arrivalTime.isAfterOrEqual(currentTime)) {
                return arrivalTime;
            }
        }
        return Time.MAX_TIME;
    }


    /**
     * Gets the last time that this train passes stationName
     *
     * @param station: the target station
     * @param currentTime: only arrival times before this time are considered
     * @param dayType an integer representing the type of day: weekday, saturday, sunday. Use of the constants in {@code Schedule}
     * @return the last time (before currentTime) that this train passed stationName. Or returns Time.MIN_TIME if the train doesn't pass this station
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if dayType is an integer that is neither 0, 1, nor 2
     */
    public Time getLastArrivalTimeAt(Station station, Time currentTime, int dayType) {
        if (! (0 <= dayType && dayType < 3))
            throw new IllegalArgumentException("dayType must be either 0, 1, or 2");

        Time[] times = schedule.get(station.name());
        Time arrivalTime = null;
        if (times != null)
            arrivalTime = times[dayType];

        if (arrivalTime != null) {
            if (arrivalTime.isBeforeOrEqual(currentTime)) {
                return arrivalTime;
            }
        }
        return Time.MIN_TIME;
    }


    /**
     * Add a stop to this train's schedule. A stop is determined by the station is arrives at and at what time.
     * @param station the station the train stops at
     * @param arrivalTime the arrival time at the station
     * @param dayType an integer representing the type of day: weekday, saturday, sunday. Use of the constants in {@code Schedule}
     * @throws NullPointerException if station is null
     * @throws IllegalArgumentException if dayType is not equal to one of 0, 1, 2
     */
    public void addStop(String station, Time arrivalTime, int dayType) {
        if (! (0 <= dayType && dayType < 3))
            throw new IllegalArgumentException("dayType must one of 0, 1, 2");

        Time[] currentTimes = this.schedule.get(station);
        if (currentTimes != null)  // if the station has already been added to the schedule
            currentTimes[dayType] = arrivalTime;
        else {
            Time[] arrivalTimes = new Time[3];
            arrivalTimes[dayType] = arrivalTime;
            this.schedule.put(station, arrivalTimes);
        }
    }

    /**
     * Returns a string representation of the train: "Train [train number], {[station name]=[arrival time], ...}"
     * @return the string representation
     */
    public String toString() {
        return "Train " + String.valueOf(this.number) + ", " + this.schedule.toString();
    }
}
