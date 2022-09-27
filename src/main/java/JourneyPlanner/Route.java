
package JourneyPlanner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue; 


/**
 * A Route object stores all the information about the route a user will take
 * between two stations including: stations passed along the way, trains taken, 
 * train arrival times. A new Route object is made per routing request made in the app.
 * 
 * @author Michael Wade
 * @see Line
 * @see Schedule
 * @see Graph
 * @see RoutingStation
 */
public class Route { 

   // Routing variables
   /**
    * The graph to use during routing. Initialised using {@code setGraph(Graph graph)}
    */
   private static Graph graph;

   /**
    * The collection of lines to use during routing
    */
   private static HashMap<String, Line> lines;


   // Output variables
   /**
    * Stations visited on shortest path
    */
   private final LinkedList<RoutingStation> stations;

   /**
    * Trains taken on shortest path
    */
   private final LinkedList<Integer> trainNumbers;

   /**
    * Arrival times at each station in {@code stations}
    */
   private final LinkedList<Time> arrivalTimes;

   private final LinkedList<String> linesUsed;


   /**
    * Sets the graph to use during routing.
    * @param graph the graph to use
    */
   public static void setGraph(Graph graph) {
      Route.graph = graph;
   }


   /**
    * Get a shallow copy of this route's graph
    * @return the graph
    */
   public static Graph graph() {
      return Route.graph;
   }


   /**
    * Sets the line schedules to use during routing.
    * @param lines the array of line objects which contain the schedules
    */
   public static void setLines(HashMap<String, Line> lines) {
      Route.lines = lines;
   }


   /**
    * Get a shallow copy of this route's line schedule
    * @return the line variable of this class
    */
   public static HashMap<String, Line> lines() {
      return Route.lines;
   }


   /**
    * Creates a new Route object
    * @throws IllegalStateException if {@code setGraph()} and {@code setLines()} have not been called prior
    */
   private Route() {
      this.stations = new LinkedList<RoutingStation>();
      this.trainNumbers = new LinkedList<Integer>(); 
      this.arrivalTimes = new LinkedList<Time>();
      this.linesUsed = new LinkedList<String>();

      if (Route.graph == null)
         throw new IllegalStateException("graph has not been initialised. Use setGraph(Graph graph)");
      if (Route.lines == null)
         throw new IllegalStateException("lines schedule has not been initialised. Use setLines(HashMap<String, Line> lines)");
   }
   
   
   /**
    * Finds the shortest route (in terms of time) between two stations in the graph
    * given the desired departure time. In other words, it finds the route with the
    * earliest arrival time at the end station, when starting at the start station.
    *
    * @param start The station to start at
    * @param end The destination station
    * @param startTime The desired departure time
    * @return a Route object containing all the information about the shortest path
    * @throws IllegalArgumentException If any of the parameters {@code start, end, endTime} are null, or {@code dayType != 0, 1 or 2}
    * @throws IllegalStateException if {@code setGraph()} and {@code setLines()} have not been called prior
    */
   public static Route getRoute_EarliestArrival(RoutingStation start, RoutingStation end, Time startTime, int dayType) {
      HashMap<String, RoutingStation> routingStations = new HashMap<String, RoutingStation>();

      if (start == null)
         throw new IllegalArgumentException("start can't be null");
      if (end == null)
         throw new IllegalArgumentException("end can't be null");
      if (startTime == null)
         throw new IllegalArgumentException("startTime can't be null");
      if (! (0 <= dayType && dayType < 3))
         throw new IllegalArgumentException("dayType must be one of 0, 1, 2");
      
      Route route = new Route();
      start.setCost(Time.MIN_TIME); // set cost to 00:00 zero time
      Time clock = startTime;
      RoutingStation endStation = null;  // placeholder for end RoutingStation

      PriorityQueue<RoutingStation> queue = new PriorityQueue<RoutingStation>();
      queue.add(start);

      while (queue.size() != 0) {
         RoutingStation S = queue.poll();
         S.setPopped(true);

         if (S.equals(end)) {   // The shortest route to end has been found
            endStation = S;
            break;
         }

         if (! S.timeArrivedAt().equals(Time.MAX_TIME)) {
            clock = S.timeArrivedAt();
         }
         
         // For each station T adjacent to S 
         for (String stationName : S.adjacentStations()) {
            // Get or create Routing station object
            RoutingStation T;
            if (routingStations.containsKey(stationName)) {
               T = routingStations.get(stationName); // get the existing routing station object
            }
            else {
               T = new RoutingStation(Route.graph.get(stationName)); // or create new routing station object
               routingStations.put(stationName, T);
            }

            if (T.popped()) {
               continue;  // if T has been popped from the queue before, then skip this one and check the next
            }

            // Time until next train, that comes from S, arrives at T
            // Also get the train number of this train 
            Time earliestArrivalTime = Time.MAX_TIME;
            int earliestTrainNumber = 0;
            String earliestLine = "";

            // Find lines which contain S and T in order, or if no such line exists, instead get all lines which contain one of S or T
            LinkedList<Line> linesWhichContainSAndT = new LinkedList<Line>();
            boolean foundALineWhichContainsBothInOrder = false;
            int containsStatus;

            // Check all lines
            for (Line line : lines.values()) {
               containsStatus = line.contains(S, T);
               if (containsStatus > 0) {
                  linesWhichContainSAndT.add(line);
                  if (containsStatus == 3)
                     foundALineWhichContainsBothInOrder = true;
               }
            }
            // If there are lines which contain both S and T in order, then remove all other lines
            if (foundALineWhichContainsBothInOrder)
               linesWhichContainSAndT.removeIf(line -> line.contains(S, T) < 3);

            // for each line
            for (Line line : linesWhichContainSAndT) {
               // for each train on this line
               for (Train train : line.schedule().trains()) {
                  Time arrivalTimeAtT = train.getNextArrivalTimeAt(T, clock, dayType); // Get the next time that the train arrives at station T
                  Time arrivalTimeAtS = train.getNextArrivalTimeAt(S, clock, dayType); // If it doesn't pass that station, it returns null

                  // Compare arrivalTime with earliestArrivalTime
                  if (arrivalTimeAtS.isBefore(arrivalTimeAtT)) {   // if the train passes S before T
                     if (arrivalTimeAtT.isBefore(earliestArrivalTime)) {  // If we've found an earlier train to take
                        earliestArrivalTime = arrivalTimeAtT;
                        earliestTrainNumber = train.number();
                        earliestLine = line.name();
                     }
                     // Update the earliest train to take from S
                     if (arrivalTimeAtS.isBefore(S.timeArrivedAt()))
                        S.setTimeArrivedAt(arrivalTimeAtS);
                  }
               }
            }

            // Compare new cost to reach T with the current T.cost()
            Time costToT = Time.subtract(earliestArrivalTime, startTime);
            if (costToT.isBefore(T.cost())) { // if we've found a new shortest route to T
               T.setPrev(S);
               T.setCost(costToT);
               T.setTimeArrivedAt(earliestArrivalTime);
               T.setPrevTrain(earliestTrainNumber);
               T.setPrevLine(earliestLine);

               queue.add(T);
            }
         }
      }
      // Extract the shortest path by repeatedly calling prev() from end station to start station
      RoutingStation prev = endStation;
      if (prev != null) {
         do {
            route.stations.addFirst(prev);
            route.trainNumbers.addFirst(prev.prevTrain());
            route.arrivalTimes.addFirst(prev.timeArrivedAt());
            route.linesUsed.addFirst(prev.prevLine());
            prev = prev.prev();
         }
         while (prev != null);
      }
      return route;
   }


   /**
    * Finds the shortest route (in terms of time) between two stations in the graph
    * given the desired arrival time. In other words, it finds the route with the latest departure time
    * to get to the end station by endTime.
    *
    * @param start The station to start at
    * @param end The destination station
    * @param endTime The desired departure time
    * @return a Route object containing all the information about the shortest path
    * @throws IllegalArgumentException If any of the parameters {@code start, end, endTime} are null, or {@code dayType != 0, 1 or 2}
    * @throws IllegalStateException if {@code setGraph()} and {@code setLines()} have not been called prior
    */
   public static Route getRoute_LatestDeparture(RoutingStation start, RoutingStation end, Time endTime, int dayType) {
      HashMap<String, RoutingStation> routingStations = new HashMap<String, RoutingStation>();

      if (start == null)
         throw new IllegalArgumentException("start can't be null");
      if (end == null)
         throw new IllegalArgumentException("end can't be null");
      if (endTime == null)
         throw new IllegalArgumentException("endTime can't be null");
      if (! (0 <= dayType && dayType < 3))
         throw new IllegalArgumentException("dayType must be one of 0, 1, 2");

      Route route = new Route();
      end.setCost(Time.MIN_TIME); // set cost to 00:00 zero time
      end.setTimeArrivedAt(Time.MIN_TIME);
      Time clock = endTime;
      RoutingStation startStation = null;  // placeholder for start RoutingStation

      PriorityQueue<RoutingStation> queue = new PriorityQueue<RoutingStation>();
      queue.add(end);

      while (queue.size() != 0) {
         RoutingStation S = queue.poll();
         S.setPopped(true);
         Time minCost = Time.MAX_TIME;

         if (! S.timeArrivedAt().equals(Time.MIN_TIME)) {
            clock = S.timeArrivedAt();
         }

         // For each station T adjacent to S
         for (String stationName : S.adjacentStations()) {
            // Get or create Routing station object
            RoutingStation T;
            if (routingStations.containsKey(stationName)) {
               T = routingStations.get(stationName); // get the existing routing station object
            }
            else {
               T = new RoutingStation(Route.graph.get(stationName)); // or create new routing station object
               T.setTimeArrivedAt(Time.MIN_TIME);
               routingStations.put(stationName, T);
            }

            if (T.popped()) {
               continue;  // if T has been popped from the queue before, then skip this one and check the next
            }

            // Find the latest arrival time of a train coming from S to T
            // Also get the train number of this train
            Time latestArrivalTime = Time.MIN_TIME;
            int latestTrainNumber = 0;
            String latestLine = "";

            // Find lines which contain S and T in order, or if no such line exists, instead get all lines which contain one of S or T
            LinkedList<Line> linesWhichContainSAndT = new LinkedList<Line>();
            boolean foundALineWhichContainsBothInOrder = false;
            int containsStatus;

            // Check all lines
            for (Line line : lines.values()) {
               containsStatus = line.contains(T, S);
               if (containsStatus > 0) {
                  linesWhichContainSAndT.add(line);
                  if (containsStatus == 3)
                     foundALineWhichContainsBothInOrder = true;
               }
            }
            // If there are lines which contain both S and T in order, then remove all other lines
            if (foundALineWhichContainsBothInOrder)
               linesWhichContainSAndT.removeIf(line -> line.contains(T, S) < 3);

            // for each line
            for (Line line : linesWhichContainSAndT) {
               // for each train on this line
               for (Train train : line.schedule().trains()) {
                  Time arrivalTimeAtT = train.getLastArrivalTimeAt(T, clock, dayType); // Get the next time that the train arrives at station T
                  Time arrivalTimeAtS = train.getLastArrivalTimeAt(S, clock, dayType); // If it doesn't pass that station, it returns null

                  // Compare arrivalTime with earliestArrivalTime
                  if (arrivalTimeAtS.isAfterOrEqual(arrivalTimeAtT)) {   // if the train passes T before S: Changed from isAfter
                     if (arrivalTimeAtT.isAfter(latestArrivalTime)) {  // If we've found a later train to take
                        latestArrivalTime = arrivalTimeAtT;
                        latestTrainNumber = train.number();
                        latestLine = line.name();
                     }
                     // Update the earliest train to take to S
                     if (arrivalTimeAtS.isAfter(S.timeArrivedAt()))
                        S.setTimeArrivedAt(arrivalTimeAtS);
                  }
               }
            }
            // Compare new cost to reach T with the current T.cost()
            if (! latestArrivalTime.equals(Time.MIN_TIME)) {
               Time costFromT = Time.subtract(endTime, latestArrivalTime);
               if (costFromT.isBefore(T.cost())) { // if we've found a new shortest route from T
                  T.setCost(costFromT);
                  T.setTimeArrivedAt(latestArrivalTime);
                  T.setPrevTrain(latestTrainNumber);
                  T.setPrevLine(latestLine);

                  T.setNext(S);
                  queue.add(T);
               }
            }
         }
         if (S.equals(start)) {   // The shortest route from start to end has been found
            startStation = S;
            break;
         }
      }
      // Extract the shortest path by repeatedly calling next() from start station to end station
      RoutingStation next = startStation;
      if (next != null) {
         do {
            route.stations.addLast(next);
            route.trainNumbers.addLast(next.prevTrain());
            route.arrivalTimes.addLast(next.timeArrivedAt());
            route.linesUsed.addLast(next.prevLine());
            next = next.next();
         }
         while (next != null);
      }
      return route;
   }


   /**
    * Get a list of the station names visited in order along the shortest path. Should only be called after
    * calling getRoute_EarliestArrival or getRoute_LatestDeparture
    * @return list of station names
    */
   public String[] stations() {
      RoutingStation[] temp = stations.toArray(new RoutingStation[0]);
      String[] result = new String[temp.length];
      for (int i = 0; i < temp.length; i++) {
         result[i] = temp[i].name();
      }
      return result;
   }


   /**
    * Get a list of the trains taken along the shortest path. Should only be called after
    * calling getRoute_EarliestArrival or getRoute_LatestDeparture
    * @return list of train numbers
    */
   public Integer[] trainNumbers() {
      return this.trainNumbers.toArray(new Integer[trainNumbers.size()]);
   }


   /**
    * Get a list of the times the train stopped along the way to the destination (i.e. at each intermediate station).
    * Should only be called after calling getRoute_EarliestArrival or getRoute_LatestDeparture
    * @return list of arrival times at each station
    */
   public Time[] arrivalTimes() {
      return this.arrivalTimes.toArray(new Time[arrivalTimes.size()]);
   }


   /**
    * Get a list of all the lines on which the trains returned by {@code trainsNumber()} travelled on
    * @return an array of all the line names. Should only be called after calling getRoute_EarliestArrival or getRoute_LatestDeparture
    */
   public String[] linesUsed() {
      return this.linesUsed.toArray(new String[linesUsed.size()]);
   }
}
