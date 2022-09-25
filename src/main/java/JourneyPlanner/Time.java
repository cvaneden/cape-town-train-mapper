
package JourneyPlanner;

/**
 * A Time in 24-hour format. Can also be using to represent an amount of time in hours and minutes.
 *
 * @author Michael Wade
 *
 */
public class Time {
 
    public int hour;
    public int minute;

    /**
     * Represents "infinite" time. That is {@code time.isBefore(MAX_TIME)} and {@code MAX_TIME.isAfter(time)} is
     * always true for any time object, {@code time}. Moreover, {@code add(time, MAX_TIME) = add(MAX_TIME, MAX_TIME) = MAX_TIME}
     * and {@code subtract(MAX_TIME, time) = MAX_TIME}
     */
    public static final Time MAX_TIME = new Time(Integer.MAX_VALUE, Integer.MAX_VALUE);

    /**
     * Represents zero time: 00:00
     */
    public static final Time MIN_TIME = new Time(0, 0);


    /**
     * Creates a time object with the specified hours and minutes 
     * 
     * @param hour hour value for time
     * @param minute minute value for time
     */
    public Time(int hour, int minute) {    
      this.hour = hour; 
      this.minute = minute; 
    }
    
    
    /**
     * Creates a time object from a string such as "02:00" 
     * 
     * @param time the string representation of the time object
     */
    public Time(String time) { 
      int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
      int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
      
      this.hour = hour; 
      this.minute = minute; 
    }
    
    /**
     * Returns true if the times are the same
     * 
     * @param other The time object to compare to
     */
    public boolean equals(Time other) {
      return (this.hour == other.hour) && (this.minute == other.minute);
    }
    
    
    /**
     * Returns -1 if calling time is before argument time. Returns 1 if calling time is
     * after argument time. Returns 0  if calling time equals argument time
     * 
     * @param other the time object to compare to
     */
    public int compareTo(Time other) {
      if      ((this.hour < other.hour) || ((this.hour == other.hour) && (this.minute < other.minute))) { return -1; }  
      else if ((this.hour > other.hour) || ((this.hour == other.hour) && (this.minute > other.minute))) { return  1; }
      else                                                                { return  0; }
    }

    /**
     * A simplification of {@code compareTo}
     * @param other the time to compare to
     * @return true, if this time occurs before other
     */
    public boolean isBefore(Time other) {
        return this.compareTo(other) == -1;
    }


    /**
     * A simplification of {@code compareTo}
     * @param other the time to compare to
     * @return true, if this time occurs after other
     */
    public boolean isAfter(Time other)  {
        return this.compareTo(other) == 1;
    }


    /**
     * A simplification of {@code compareTo}
     * @param other the time to compare to
     * @return true, if this time occurs before other, or is equal to other
     */
    public boolean isBeforeOrEqual(Time other) {
        return ((this.compareTo(other) == -1) || (this.compareTo(other) == 0));
    }


    /**
     * A simplification of {@code compareTo}
     * @param other the time to compare to
     * @return true, if this time occurs after other, or is equal to other
     */
    public boolean isAfterOrEqual(Time other) {
        return ((this.compareTo(other) == 1) || (this.compareTo(other) == 0)); }


    /**
     * Returns the time object which is the sum of the two given times. If either {@code t1} or {@code t2} are
     * {@code MAX_TIME}, then this returns {@code MAX_TIME}
     * 
     * @param t1: first time to add 
     * @param t2: second time to add
     * @return the time object which represent the time {@code t1 + t2}
     * @throws NullPointerException if either of the parameters are null
     */
    public static Time add(Time t1, Time t2) {
        if (t1.equals(MAX_TIME) || t2.equals(MAX_TIME))
            return MAX_TIME;
        return new Time(t1.hour + t2.hour, (t1.minute + t2.minute) % 60);
    }


    /**
     * Returns the time object that is t1 - t2. If {@code t1 = MAX_TIME}, then it returns
     * {@code MAX_TIME} (infinity - constant = infinity).
     * 
     * @param t1: time object to subtract from 
     * @param t2: amount of time to subtract
     * @return the time object that represents {@code t1 - t2}
     * @throws NullPointerException if either of the parameters are null
     */
    public static Time subtract(Time t1, Time t2) {
        if (t1.equals(MAX_TIME)) {  // the idea is that: infinity - constant = infinity
            return MAX_TIME;
        }
        Time result = null;
        int hourDifference = t1.hour - t2.hour;

        if (hourDifference < 0) {
           hourDifference += 24;
        }

        int minuteDifference = t1.minute - t2.minute;

        if (minuteDifference < 0) {
           hourDifference--;
           minuteDifference += 60;
        }
           
         return new Time(hourDifference % 24, minuteDifference % 60); 
    }
    
    
    /**
     * Returns the string representation of the time in 24-hour format 
     * 
     * @return string representation of the time in 24-hour format
     */
    public String toString() {
      String hourString = String.valueOf(hour % 24);
      String minuteString = String.valueOf(minute);
      
      if (minute < 10) {
         minuteString = "0" + minuteString;     
      }
      if (hour < 10) {
         hourString = "0" + hourString;
      }
      return hourString + ":" + minuteString;
    }
}
