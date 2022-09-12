
package JourneyPlanner;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * A Route object stores all the information about the route a user will take
 * between two stations including: stations passed along the way, trains taken, 
 * train arrival times. A new Route object is made per routing request made in the app.
 * 
 * @author Michael Wade
 * @version 3 Sep 2022, 1:40pm
 */
public class Route {

   // Routing variables
   private static Graph graph;
   private static Schedule schedule;

   // Output variables
   private final LinkedList<RoutingStation> stations;
   //private final LinkedList<Integer> platformNumbers; // platform number that the train arrives at, at each station
   private final LinkedList<Integer> trainNumbers;
   private final LinkedList<Time> arrivalTimes;


   /**
    * Overrides the default graph to use during routing.
    * @param graph the graph to use
    */
   public static void setGraph(Graph graph) {
      Route.graph = graph;
   }


   /**
    * Overrides the default schedule to use during routing.
    * @param schedule the schedule to use
    */
   public static void setSchedule(Schedule schedule) {
      Route.schedule = schedule;
   }


   /**
    * Creates a new Route object and sets the default schedule and graph
    */
   private Route() {
      this.stations = new LinkedList<RoutingStation>(); 
      //this.platformNumbers = new LinkedList<Integer>(); 
      this.trainNumbers = new LinkedList<Integer>(); 
      this.arrivalTimes = new LinkedList<Time>();

      if (Route.graph == null)
         Route.graph = new Graph();
      if (Route.schedule == null)
         Route.schedule = new Schedule();
   }
   
   
   /**
    * Finds the shortest route (in terms of time) between two stations in the graph
    * given the desired departure time. In other words, it finds the route with the
    * earliest arrival time at the end station, when starting at the start station.
    *
    * @param start The station to start at
    * @param end The destination station
    * @param startTime The desired departure time
    *
    */
   public static Route getRoute_EarliestArrival(RoutingStation start, RoutingStation end, Time startTime) {
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
            RoutingStation T = new RoutingStation(Route.graph.get(stationName));
            if (T.popped()) {
               continue;  // if T has been popped from the queue before, then skip this one and check the next
            }

            // Time until next train, that comes from S, arrives at T
            // Also get the train number of this train 
            Time earliestArrivalTime = Time.MAX_TIME;
            int earliestTrainNumber = 0; 

            // First check the current train (the one the user is currently on)
            boolean checkOtherTrains = true;
            if (S.prevTrain() != 0) {  // If S isn't the starting station i.e. there is a current train
               Train currentTrain = Route.schedule.getTrain(S.prevTrain());  // get the current train (its train number)

               Time currentTrainArrivalAtT = currentTrain.getNextArrivalTimeAt(T, clock); // get the time the train arrives at T, or null if it doesn't
               if (! currentTrainArrivalAtT.equals(Time.MAX_TIME)) { // if the current train passes T
                  earliestArrivalTime = currentTrainArrivalAtT;
                  earliestTrainNumber = S.prevTrain();
                  checkOtherTrains = false;
               }
            }
            // Otherwise, check all the other trains
            if (checkOtherTrains) {
               for (Train train : Route.schedule.trains()) {
                  Time arrivalTimeAtT = train.getNextArrivalTimeAt(T, clock); // Get the next time that the train arrives at station T
                  Time arrivalTimeAtS = train.getNextArrivalTimeAt(S, clock); // If it doesn't pass that station, it returns null

                  // Compare arrivalTime with earliestArrivalTime
                  if (arrivalTimeAtS.isBefore(arrivalTimeAtT)) {   // if the train passes S before T
                     if (arrivalTimeAtT.isBefore(earliestArrivalTime)) {  // If we've found an earlier train to take
                        earliestArrivalTime = arrivalTimeAtT;
                        earliestTrainNumber = train.number();
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

               queue.add(T);
            }
         }
      }
      // Extract the shortest path recursively
      route.extractShortestPath(endStation); 
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
    *
    */
   public static Route getRoute_LatestDeparture(RoutingStation start, RoutingStation end, Time endTime) {
      Route route = new Route();
      end.setCost(Time.MIN_TIME); // set cost to 00:00 zero time
      end.setTimeArrivedAt(Time.MIN_TIME);
      Time clock = endTime;
      RoutingStation endStation = null;  // placeholder for end RoutingStation

      PriorityQueue<RoutingStation> queue = new PriorityQueue<RoutingStation>();
      queue.add(end);

      while (queue.size() != 0) {
         RoutingStation S = queue.poll();
         S.setPopped(true);
         Time minCost = Time.MAX_TIME;

         if (S.equals(start)) {   // The shortest route to end has been found
            endStation = end;
            break;
         }

         if (! S.timeArrivedAt().equals(Time.MIN_TIME)) {
            clock = S.timeArrivedAt();
         }

         // For each station T adjacent to S
         for (String stationName : S.adjacentStations()) {
            RoutingStation T = new RoutingStation(Route.graph.get(stationName));
            T.setTimeArrivedAt(Time.MIN_TIME);
            if (T.popped()) {
               continue;  // if T has been popped from the queue before, then skip this one and check the next
            }

            // Find the latest arrival time of a train coming from S to T
            // Also get the train number of this train
            Time latestArrivalTime = Time.MIN_TIME;
            int latestTrainNumber = 0;

            for (Train train : Route.schedule.trains()) {
               Time arrivalTimeAtT = train.getLastArrivalTimeAt(T, clock); // Get the next time that the train arrives at station T
               Time arrivalTimeAtS = train.getLastArrivalTimeAt(S, clock); // If it doesn't pass that station, it returns null

               // Compare arrivalTime with earliestArrivalTime
               if (arrivalTimeAtS.isAfter(arrivalTimeAtT)) {   // if the train passes T before S
                  if (arrivalTimeAtT.isAfter(latestArrivalTime)) {  // If we've found a later train to take
                     latestArrivalTime = arrivalTimeAtT;
                     latestTrainNumber = train.number();
                  }
                  // Update the earliest train to take from S
                  if (arrivalTimeAtS.isAfter(S.timeArrivedAt()))
                     S.setTimeArrivedAt(arrivalTimeAtS);
               }
            }

            // Compare new cost to reach T with the current T.cost()
            if (! latestArrivalTime.equals(Time.MIN_TIME)) {
               Time costFromT = Time.subtract(endTime, latestArrivalTime);
               if (costFromT.isBefore(T.cost())) { // if we've found a new shortest route from T
                  T.setCost(costFromT);
                  T.setTimeArrivedAt(latestArrivalTime);
                  T.setPrevTrain(latestTrainNumber);

                  if (T.cost().isBefore(minCost)) {
                     minCost = T.cost();
                     S.setPrev(T);
                  }

                  queue.add(T);
               }
            }
         }
      }
      // Extract the shortest path recursively
      route.extractShortestPath(endStation);
      return route;
   }


   /**
    * Extract all the stations visited and trains taken on the shortest path and add them 
    * to the 'stations' and 'trainNumbers' instance variables respectively 
    * 
    * @param V: The end station on the route
    */
   private void extractShortestPath(RoutingStation V) {
      if (V != null) { 
         this.stations.addFirst(V); 
         this.trainNumbers.addFirst(V.prevTrain()); 
         this.arrivalTimes.addFirst(V.timeArrivedAt()); 
         
         this.extractShortestPath(V.prev()); 
      }
   }


   /**
    * Get a list of the station names visited in order along the shortest path. Should only be called after
    * calling getRoute_EarliestArrival
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
    * calling getRoute_EarliestArrival
    * @return list of train numbers
    */
   public Integer[] trainNumbers() {
      return this.trainNumbers.toArray(new Integer[trainNumbers.size()]);
   }


   /**
    * Get a list of the times the train stopped along the way to the destination (i.e. at each intermediate station)
    * @return list of arrival times at each station
    */
   public Time[] arrivalTimes() {
      return this.arrivalTimes.toArray(new Time[arrivalTimes.size()]);
   }
}
