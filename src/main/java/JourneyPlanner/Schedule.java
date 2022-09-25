
package JourneyPlanner;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class creates the schedule used for the routing algorithm.
 * Reads information in from a subset of text from a csv file and creates the schedule
 *
 * @author Michael Wade
 * @see Train
 */
public class Schedule {
    private HashMap<Integer, Train> schedule;

    /**
     * Index for weekday times
     */
    public static final int WEEKDAYS = 0;

    /**
     * Index for Saturday times
     */
    public static final int SATURDAYS = 1;

    /**
     * Index for Sunday times
     */
    public static final int SUNDAYS = 2;

    /*
     * Some useful methods for classifying different types of text that can be found in the csv file.
     * Makes the code in the String-arg constructor simpler.
     */

    /**
     * Checks if the given string is a station name such as "CAPE TOWN"
     * @param text the text to check
     * @return true if {@code text} is a station name, false otherwise
     */
    private static boolean isStationName(String text) {
        text = text.trim();
        // if text satisfies one of the following return false. Note: text.matches("\\d") means: is text a string of an integer
        return !(text.length() < 3  || text.equals(". .") || text.matches("\\d+") ||
                text.contains("-") || text.contains(":") || text.contains("DAY") || text.contains("PLATFORM") ||
                text.contains("TRAIN") || text.equals("END") || text.equals("END OF FILE"));
    }


    /**
     * Checks if the given text is a time such as "12:46"
     * @param text the text to check
     * @return true if {@code text} is a time
     */
    private static boolean isTime(String text) {
        text = text.trim();
        return text.contains(":");
    }


    /**
     * Checks if the given text is one of "MONDAYS TO FRIDAYS", "SATURDAYS", "SUNDAYS"
     * @param text the text to check
     * @return true, if {@code text} satisfies the condition, false otherwise
     */
    private static boolean isDayOfWeek(String text) {
        text = text.trim();
        return (text.equals("MONDAYS TO FRIDAYS") || text.equals("SATURDAYS") || text.equals("SUNDAYS AND PUBLIC HOLIDAYS") || text.equals("SUNDAYS"));
    }

    /**
     * <p>Creates a schedule object from a portion of text from a csv file.
     * <p>Format:
     * <p>NEW LINE;
     * <p>MONDAYS TO FRIDAYS;
     * <p>TRAIN NO.;[train 1];[train 2];...;
     * <p>[Station 1];[arrival time 1];[arrival time 2];...;
     * <p>[Station 2];[arrival time 1];[arrival time 2];...;
     * <p>...
     * <p>[Station n];[arrival time 1];[arrival time 2];...;
     * <p>END;
     * <p>SATURDAYS;
     * <p>TRAIN NO.; ...
     * <p>...
     * <p>END;
     * <p>SUNDAYS;
     * <p>...
     * <p>END;
     * <p>END OF FILE
     *
     * <p>Notes: there is no semicolon after the last "END", "SUNDAY" may instead be "SUNDAYS AND PUBLIC HOLIDAYS",
     * a blank arrival time (in fact any string which does not contain ":") means the train doesn't pass the station,
     * any extra text such as blank cells or newline characters between "NEW LINE", [DAY TYPE] "TRAIN NO." are ignored.
     * @param csvText the formatted text
     * @throws IndexOutOfBoundsException if csvText is incorrectly formatted
     */
    public Schedule(String csvText) {
        this.schedule = new HashMap<Integer, Train>();
        String[] csvArray = csvText.split(";");
        int i = 0;
        int day;

        String[] prevEntries = new String[5];

        for (int n = 0; n < 3; n++) {  // Repeat three times: once for weekdays, once for saturdays and once of sundays
            LinkedList<Integer> trains = new LinkedList<Integer>();
            // Find day type
            while (!isDayOfWeek(csvArray[i])) {
                i++;
            }

            // Set day type
            if (csvArray[i].equals("MONDAYS TO FRIDAYS"))
                day = WEEKDAYS;
            else if (csvArray[i].equals("SATURDAYS"))
                day = SATURDAYS;
            else
                day = SUNDAYS;

            // Find the train numbers
            while (! csvArray[i].equals("TRAIN NO.")) {
                i++;
            }
            i++;

            // Read all the train numbers
            // Make all the train objects with empty schedules for now
            int trainNumber;

            while (csvArray[i].matches("\\d+") || csvArray[i].trim().equals("")) {  // while the next entry is a string of an integer e.g. "101". "\\d+" is regex code
                if (csvArray[i].matches("\\d+")) {
                    trainNumber = Integer.parseInt(csvArray[i]);
                    if (!trains.contains(trainNumber))   // if the train hasn't already been added to the schedule
                        trains.add(trainNumber);

                    if (this.schedule.get(trainNumber) == null)
                        this.schedule.put(trainNumber, new Train(trainNumber));
                }
                i++;
            }

            // For each station S and for each train t which arrives at that station. Add S as a stop to t's schedule
            String stationName;
            int trainIndex;

            forEachStation:  // label for outer loop
            while (isStationName(csvArray[i])) {
                stationName = csvArray[i];
                if (csvArray[i].equals("END"))
                    break;  // break out of loop and continue to next day type in outermost for loop

                i++;
                trainIndex = 0;
                while (! isStationName(csvArray[i])) {  // i.e. while it's a time or ".." or "" meaning it does not arrive at this station
                    if (isTime(csvArray[i])) {
                        this.schedule.get(trains.get(trainIndex)).addStop(stationName, new Time(csvArray[i]), day);
                    }

                    if (csvArray[i].equals("END"))
                        break forEachStation;  // break out of outer loop and continue to next day type in outermost for loop

                    if (i < csvArray.length - 1) {
                        i++;
                    }
                    trainIndex++;
                }
            }
        }
    }


    /**
     * Create a blank schedule object. Can use addTrain() to add trains to the stations
     */
    public Schedule() {
        this.schedule = new HashMap<Integer, Train>();
    }


    /**
     * Get the train object from the schedule with the given train number
     * @param trainNumber the train's number
     * @return the train
     */
    public Train getTrain(int trainNumber) {
	    return this.schedule.get(trainNumber);
    }


    /**
     * Get an array of all the trains in this schedule
     *
     * @return the array of trains
     */
    public Collection<Train> trains() {
        return this.schedule.values();
    }


    /**
     * Add a train to the schedule
     * @param train the train to add
     * @throws NullPointerException if train is null
     */
    public void addTrain(Train train) {
        this.schedule.put(train.number(), train);
    }


    /**
     * Remove a train from the schedule if it is in the schedule
     * @param train the train to remove
     * @return true, if the train was removed. false, if the train isn't in the schedule
     */
    public boolean removeTrain(Train train) {
        return this.schedule.remove(train.number(), train);
    }


    /**
     * Returns a string representation of this schedule
     * @return the string representation
     */
    public String toString() {
        return this.schedule.toString();
    }

}
