package org.ulitzky.flights.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ulitzky.flights.model.*;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by lulitzky on 19.04.18.
 */
@Service
@Slf4j
public class FlightScheduleBuildingService {


    /*
     * Build the flight schedule for given flights and initial locations of the airfleet.
     *
     * @return list of flight assignments, if the solution is found
     * @return Optional.empty(), if the schedule cannot be built
     */
    public Optional<List<FlightAssignment>> buildFlilghtAssignments(final List<AircraftLocation> homeBaseList, final List<Flight> flightsToCover) {

        FlightScheduleState initialState = new FlightScheduleState(new LinkedList<FlightAssignment>(), homeBaseList, flightsToCover);

        FlightScheduleState solution =  buildFlilghtAssignments(initialState);
        if (solution == null) {
            log.error("Cannot build the flight schedule");
            return Optional.empty();
        } else {
            return Optional.of(solution.getScheduledFlights());
        }

    }

    /*
     * Recursilvely builds the flighht schedule for the given state.
     *  The algorithm is like follows:
     *    - If no unscheduled flights left, return the already built schedule (from the state) asthe solution
     *    - Take the earliest unschedule flight and try to schedule it by all possible way.
     *    - For each possible schedule, create the new state, adding the assignment to the schedule and run this method recursivley for the new state
     *    - If no possible assignment exists for the flight, try to bring the fere aircraft o the origin in time - that will cost the additional flight,
     *      but will allow to build the schedule
     *    - If it does not help - return null, as no solution exists for this state
     *
     *    @return the new state with scheduled flights, if solution exists.
     *    @return null, if the solutino does not exists for this state
     *
     */
    FlightScheduleState buildFlilghtAssignments(final FlightScheduleState state) {
        log.info("Building flight assignments, number of alredy scheduled flights {}, flights to schedule {}", state.getScheduledFlights().size(), state.getFlightsToCover().size());
        if (state.getFlightsToCover().isEmpty()) {
            return state;
        } else {
            TreeMap<Date, List<Flight>> flightsMap = mapFlightsByDepartureTime(state.getFlightsToCover());

            Date currTime = flightsMap.firstKey();
            List<Flight> currFlights = flightsMap.firstEntry().getValue();

            state.getAircraftLocations().stream().filter(l -> l.getTime() == null).forEach(l -> l.setTime(currTime));

            for (Flight currFlight : currFlights) {
                List<AircraftLocation> possibleMatches = findMatchingAircrafts(currFlight, state.getAircraftLocations());
                log.debug("Number of possible matches fot flight {} -  {}", currFlight, possibleMatches.size());

                for (AircraftLocation match : possibleMatches) {
                    FlightScheduleState newState = buildNewState(state, currFlight, match);
                    FlightScheduleState processedState = buildFlilghtAssignments(newState);
                    if ((processedState != null) && processedState.getFlightsToCover().isEmpty()) {
                        return processedState;
                    }
                }
                List<FlightScheduleState> artificalStates = calcMoveAircraftStates(state, currFlight);
                for (FlightScheduleState artificialState : artificalStates) {
                    possibleMatches = findMatchingAircrafts(currFlight, artificialState.getAircraftLocations());
                    for (AircraftLocation match : possibleMatches) {
                        FlightScheduleState newState = buildNewState(artificialState, currFlight, match);
                        FlightScheduleState processedState = buildFlilghtAssignments(newState);
                        if ((processedState != null) && processedState.getFlightsToCover().isEmpty()) {
                            return processedState;
                        }
                    }
                }
            }
        }

        return null;
    }


    /*
     * @return list of all possible states, that the schedule of given flight, in all possible ways, can lead to
     */
    List<FlightScheduleState> calcMoveAircraftStates(final FlightScheduleState state, final Flight flightToServe) {
        List<FlightScheduleState> resultList = new LinkedList<>();
        for(AircraftLocation location : state.getAircraftLocations()) {
            if (location.getTime().before(flightToServe.getDepartureTime())) {
                OptionalInt flightTimeToMove = state.findFlightDuration(location.getAirport(), flightToServe.getOrigin());
                if (flightTimeToMove.isPresent()) {
                    Calendar timeToBeReady = Calendar.getInstance();
                    timeToBeReady.setTime(location.getTime());
                    timeToBeReady.add(Calendar.MINUTE, flightTimeToMove.getAsInt());

                    if (!timeToBeReady.getTime().after(flightToServe.getDepartureTime())) {
                        resultList.add(moveAircraft(state, location, flightToServe.getOrigin(), timeToBeReady.getTime()));
                    }
                }
            }
        }
        return resultList;
    }

     /*
     * Build the new state, adding the new schedule for the given flight by the given aircraft
     */
    FlightScheduleState buildNewState(final FlightScheduleState oldState, final Flight flightToCover, final AircraftLocation matchingAircraft) {
        List<FlightAssignment> newScheduledFlightsList = new LinkedList<>();
        newScheduledFlightsList.addAll(oldState.getScheduledFlights());
        newScheduledFlightsList.add(new FlightAssignment(flightToCover, matchingAircraft.getAircraft()));


        List<AircraftLocation> newAircraftLocations = new LinkedList<>();
        newAircraftLocations.addAll(oldState.getAircraftLocations());
        newAircraftLocations.remove(matchingAircraft);
        newAircraftLocations.add(new AircraftLocation(matchingAircraft.getAircraft(), flightToCover.getDestination(), flightToCover.getScheduledArrivalTime() ));


        List<Flight> newFlightsToCover = new LinkedList<>();
        newFlightsToCover.addAll(oldState.getFlightsToCover());
        newFlightsToCover.remove(flightToCover);

        return new FlightScheduleState(newScheduledFlightsList, newAircraftLocations, newFlightsToCover);
    }


    /*
     * Build the new state, transfering one of the planes from its curen location to new one, by the given time
     */
    FlightScheduleState moveAircraft(final FlightScheduleState oldState, final AircraftLocation aircraftToMove, Airport locationToMove, Date flightEndTime) {
        List<FlightAssignment> newScheduledFlightsList = new LinkedList<>();
        newScheduledFlightsList.addAll(oldState.getScheduledFlights());
        newScheduledFlightsList.add(new FlightAssignment(new Flight(aircraftToMove.getAirport(), locationToMove, aircraftToMove.getTime(), 0), aircraftToMove.getAircraft()));

        List<AircraftLocation> newAircraftLocations = new LinkedList<>();
        newAircraftLocations.addAll(oldState.getAircraftLocations());
        newAircraftLocations.remove(aircraftToMove);
        newAircraftLocations.add(new AircraftLocation(aircraftToMove.getAircraft(), locationToMove, flightEndTime ));

        List<Flight> newFlightsToCover = new LinkedList<>();
        newFlightsToCover.addAll(oldState.getFlightsToCover());

        return new FlightScheduleState(newScheduledFlightsList, newAircraftLocations, newFlightsToCover);
    }


    /*
     * @return list of all the airplanes, that can serve the given flight
     */
    private List<AircraftLocation> findMatchingAircrafts(final Flight flight, final List<AircraftLocation> homeBaseList) {
        List<AircraftLocation> result = new LinkedList<>();
        for (AircraftLocation aircraftLocation : homeBaseList) {
            if  ( (! flight.getDepartureTime().before(aircraftLocation.getTime())) &&
                    flight.getOrigin().equals(aircraftLocation.getAirport())) {
                result.add(aircraftLocation);
            }
        }
        return result;
    }

    /*
     * Map the flights by the departure types
     *
     * @return the sorted map, mapping the departure time to the list of flights, departing at that time.
     * Map is sorted by departure time, the earliest flights are first.
     */
    static private TreeMap<Date, List<Flight>> mapFlightsByDepartureTime(final List<Flight> flightsList) {
        TreeMap<Date, List<Flight>> resultMap = new TreeMap<>();
        resultMap.putAll(flightsList.stream().collect(groupingBy(Flight::getDepartureTime)));

        return resultMap;
    }


    /*
     * Auxiliary class, holding the state of the system, including
     *    - All already scheduled flights
     *    - locations of the fleet aircrafts after the scheduled flights compeletion.
     *    - list of the flights to be scheduled yet.
     *
     *  In the initial state, the scheduled flights list is empty.
     *  If the list of flights to cover is empty, the schedule is ready
     *
     *  This class is immutable, the changes in the stae (like scheduling the flight) lead to creation of the new state.
     */
    class FlightScheduleState {
        private final List<FlightAssignment> scheduledFlights;

        private final List<AircraftLocation> aircraftLocations;

        private final  List<Flight> flightsToCover;

        public FlightScheduleState(final List<FlightAssignment> scheduledFlights, final List<AircraftLocation> aircraftLocations,  List<Flight> flightsToCover) {
            this.scheduledFlights = scheduledFlights;
            this.aircraftLocations = aircraftLocations;
            this.flightsToCover = flightsToCover;
        }

        public List<FlightAssignment> getScheduledFlights() {
            return scheduledFlights;
        }

        public List<AircraftLocation> getAircraftLocations() {
            return aircraftLocations;
        }

        public List<Flight> getFlightsToCover() {
            return flightsToCover;
        }

        OptionalInt findFlightDuration(final Airport origin, final Airport destination) {
            OptionalInt result = calcFlightDurationOneWay(origin, destination);
            if (result.isPresent()) {
                return result;
            } else {
                return calcFlightDurationOneWay(destination, origin);
            }
        }


        private OptionalInt calcFlightDurationOneWay(final Airport origin, final Airport destination) {
            OptionalInt resultInFlightsToCover =
                    flightsToCover.parallelStream()
                                   .filter( f -> f.getOrigin().equals(origin) && f.getDestination()
                                   .equals(destination))
                                   .mapToInt( f -> f.getFlightLength())
                                   .max();

            if (resultInFlightsToCover.isPresent()) {
                return resultInFlightsToCover;
            } else {
                return scheduledFlights.parallelStream()
                                        .map(f -> f.getFlight())
                                        .filter( f -> f.getOrigin().equals(origin) && f.getDestination().equals(destination))
                                        .mapToInt( f -> f.getFlightLength())
                                        .max();
            }
        }

    }

}
