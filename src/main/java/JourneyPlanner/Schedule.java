
package JourneyPlanner;

import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * This class creates the schedule used for the routing algorithm
 * Reads information in from a text file and proceeds to create hashtables to store the information
 * 
 * @author Zac Schmidt, Michael Wade 
 * @version 17 Aug 2022, 5:03pm
 */

public class Schedule {
    private Hashtable<Integer, Train> schedule;

    /**
     * Creates a schedule object from information in a text file
     * @param textfile name of the text file
     */
	public Schedule(String textfile) {
	  File myObj = new File(textfile);
	  Scanner myReader = null;
      try {
          myReader = new Scanner(myObj);
      }
      catch (FileNotFoundException e) {
          e.printStackTrace();
      }
       
      String trains = myReader.nextLine();
       
      int[] trainNums = new int[30];
      int x = 0;
       
      //gets train numbers from text file
      for (int i = 2; i < trains.length(); i += 7) {
         trainNums[x] = Integer.parseInt(trains.substring(i, i+1));
       	
       	x++;
      }
       
      this.schedule = new Hashtable<Integer, Train>();
       
      Hashtable<String, Time> train1 = new Hashtable<String, Time>();
      Train train11 = new Train(trainNums[0], train1);
       
      Hashtable<String, Time> train2 = new Hashtable<String, Time>();
      Train train22 = new Train(trainNums[1], train2);
       
      Hashtable<String, Time> train3 = new Hashtable<String, Time>();
      Train train33 = new Train(trainNums[2], train3);
       
      Hashtable<String, Time> train4 = new Hashtable<String, Time>();
      Train train44 = new Train(trainNums[3], train4);
       
      //read in the schedules from the text files and update the schedule hash table
      while (myReader.hasNextLine()) {
         String data = myReader.nextLine();
         String station = data.substring(0,1);
         
         //create a time object and then proceed to add to the train and schedule hash tables
         if (! data.substring(2,7).equals("     ")) { 
            Time time1 = new Time(data.substring(2,7).trim()); 
       	   train1.put(station, time1);
       	  
       	   schedule.put(trainNums[0], train11);
         }
         
         if (! data.substring(9, 14).equals("     ")) { 
            Time time2 = new Time(data.substring(9,14).trim()); 
       	   train2.put(station, time2);
       	  
       	   schedule.put(trainNums[1], train22);
         }
         
         if (! data.substring(16, 21).equals("     ")) { 
            Time time3 = new Time(data.substring(16,21).trim()); 
       	   train3.put(station, time3);
       	  
       	   schedule.put(trainNums[2], train33);
         }
         
         if (! data.substring(23, 28).equals("     ")) {
            Time time4 = new Time(data.substring(23,28).trim()); 
       	   train4.put(station, time4);
       	  
       	   schedule.put(trainNums[3], train44);
         }
   	  }
	}

    public Schedule() {
        this(0);
    }

	public Schedule(int numberOfTestTrains) {
        this.schedule = new Hashtable<Integer, Train>();
        // The usual four trains
        this.schedule.put(1, new Train(1, makeHashTable("A 08:00, B 08:30, C 09:20, D 09:50, E 10:00, F 10:30")));
        this.schedule.put(2, new Train(2, makeHashTable("A 10:30, B 12:30, C 11:30, F 11:10, G 10:10, H 09:00")));
        this.schedule.put(3, new Train(3, makeHashTable("A 09:00, C 07:00, D 07:30, E 07:40, F 08:20, G 09:20, H 10:30")));
        this.schedule.put(4, new Train(4, makeHashTable("B 08:10, C 06:40, D 07:10, E 07:20, F 08:00")));

        this.schedule.put(5, new Train(5, makeHashTable("F 08:10, C 07:50")));

        // Extra trains which do nothing but make lookup times slower
        for (int n = 5; n < numberOfTestTrains + 5; n++) {
            this.schedule.put(n, new Train(n, makeHashTable("A 99:00, B 99:00, C 99:00, D 99:00, E 99:00, F 99:00, G 99:00, H 99:00")));
        }
    }

    /**
     * Make a hash table from a formatted string
     * Format: <station name 1> <arrival time 1>, <station name 2> <arrival time 2>, ...
     * @param text the formatted string
     */
    private Hashtable<String, Time> makeHashTable(String text) {
        Hashtable<String, Time> t = new Hashtable<String, Time>();
        String[] items = text.split(", ");
        for (String item : items) {
            t.put(item.substring(0, item.indexOf(" ")), new Time(item.substring(item.indexOf(" ") + 1)));
        }
        return t;
    }

	public String toString() {
        return this.schedule.toString();
    }

    public Train[] trains() {
	    return this.schedule.values().toArray(new Train[0]);
   }

   public Train getTrain(int trainNumber) {
	    return this.schedule.get(trainNumber);
   }
}
