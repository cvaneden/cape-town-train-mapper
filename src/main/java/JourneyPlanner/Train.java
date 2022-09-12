
package JourneyPlanner;

import java.util.Hashtable;

/**
 * Represents a train which moves through a map of stations according to a fixed schedule.
 *
 * @author Michael Wade
 * @version 17 Aug 2022, 4:22pm
 */
public class Train {
    private int number;
    private Hashtable<String, Time> schedule;  // may need to add multiple schedules e.g. one for Mon-Fri and one for Sun
                                             // Just leaving this for now while we're using the small example schedule, and not the full Cape Town one
                                             // If a train passes a station multiple time then we will have to

    /**
     * Create a new Train object
     * @param number: the unique train number
     * @param schedule: the schedule for this train
     */
    public Train(int number, Hashtable<String, Time> schedule) {
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
     * @param station: the target station
     * @param currentTime: only arrival times after this time are considered
     * @return the next time (after currentTime) that this train passes stationName. Or returns Time.MAX_TIME if the train doesn't pass this station.
     */
    public Time getNextArrivalTimeAt(Station station, Time currentTime) {
        Time arrivalTime = schedule.get(station.name());
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
     * @return the last time (before currentTime) that this train passed stationName. Or returns Time.MIN_TIME if the train doesn't pass this station.
     */
    public Time getLastArrivalTimeAt(Station station, Time currentTime) {
        Time arrivalTime = schedule.get(station.name());
        if (arrivalTime != null) {
            if (arrivalTime.isBeforeOrEqual(currentTime)) {
                return arrivalTime;
            }
        }
        return Time.MIN_TIME;
    }


    public String toString() {
        return "Train " + String.valueOf(this.number) + ", " + this.schedule.toString();
    }
}
