package org.ulitzky.flights.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.ulitzky.flights.api.v1.resource.FlightAssignmentResource;
import org.ulitzky.flights.api.v1.resource.OperatingInstructionResource;
import org.ulitzky.flights.model.Aircraft;
import org.ulitzky.flights.model.AircraftLocation;
import org.ulitzky.flights.model.Flight;
import org.ulitzky.flights.model.FlightAssignment;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by lulitzky on 20.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlightsServiceTest {

    @InjectMocks
    private FlightsService service;

    @Mock
    private FlightScheduleBuildingService flightScheduleBuildingService;

    @Spy
    private FlightsDataMapper mapper  = Mappers.getMapper(FlightsDataMapper.class);

    List<FlightAssignment> schedule;

    @Before
    public void setUp() throws IOException {
        service.setFlightsFilename("/flights.csv");
        service.setHomebaseFilename("/homebases.csv");

        schedule = loadMockSchedule();

        when(flightScheduleBuildingService.buildFlilghtAssignments(any(List.class), any(List.class))).thenReturn(Optional.of(schedule));
        service.buildFlightSchedule();
    }

    private List<FlightAssignment> loadMockSchedule() throws IOException {
        List<FlightAssignment> schedule = new LinkedList<>();

        schedule.add(new FlightAssignment(new Flight(CSVParser.parse("10:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()),
                                            new Aircraft("FL-0001", "737")));
        schedule.add(new FlightAssignment(new Flight(CSVParser.parse("12:00,TXL,MUC,01:00", CSVFormat.DEFAULT).iterator().next()),
                new Aircraft("FL-0001", "737")));
        schedule.add(new FlightAssignment(new Flight(CSVParser.parse("14:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()),
                new Aircraft("FL-0001", "737")));

        schedule.add(new FlightAssignment(new Flight(CSVParser.parse("10:00,LHR,HAM,03:00", CSVFormat.DEFAULT).iterator().next()),
                new Aircraft("FL-0002", "A320")));

        return schedule;
    }


    @Test
    public void testLoadHomeBase() throws IOException {
        List<AircraftLocation> homebase = service.loadHomeBase();

        assertEquals(4, homebase.size());

        for (AircraftLocation location : homebase) {
            assertNotNull(location.getAircraft());
            assertNotNull(location.getAirport());
            assertNull(location.getTime());
        }

        // Check that all the aircraft registrations are unique
        Set<String> aircraftRegistrations = homebase.stream().map(l -> l.getAircraft().getRegistration()).collect(Collectors.toSet());
        assertEquals(4, aircraftRegistrations.size());

        // Check that all the airports  are unique
        Set<String> airports = homebase.stream().map(l -> l.getAirport().getCode()).collect(Collectors.toSet());
        assertEquals(4, airports.size());
    }


    @Test(expected = Exception.class)
    public void testLoadHomeBaseFileNotExists() throws IOException {
        service.setHomebaseFilename("/homebases_notexists.csv");
        List<AircraftLocation> homebase = service.loadHomeBase();
    }

    @Test(expected = Exception.class)
    public void testLoadHomeBaseInvalid() throws IOException {
        service.setHomebaseFilename("/homebases_invalid.csv");
        List<AircraftLocation> homebase = service.loadHomeBase();
    }

    @Test
    public void testLoadFlights() throws IOException {
        List<AircraftLocation> homebase = service.loadHomeBase();

        assertEquals(4, homebase.size());

        for (AircraftLocation location : homebase) {
            assertNotNull(location.getAircraft());
            assertNotNull(location.getAirport());
            assertNull(location.getTime());
        }

        // Check that all the aircraft registrations are unique
        Set<String> aircraftRegistrations = homebase.stream().map(l -> l.getAircraft().getRegistration()).collect(Collectors.toSet());
        assertEquals(4, aircraftRegistrations.size());

        // Check that all the airports  are unique
        Set<String> airports = homebase.stream().map(l -> l.getAirport().getCode()).collect(Collectors.toSet());
        assertEquals(4, airports.size());
    }


    @Test(expected = Exception.class)
    public void testLoadFlightsFileNotExists() throws IOException {
        service.setFlightsFilename("/flights_notexists.csv");
        List<Flight> flights = service.loadFlightSchedule();
    }


    @Test(expected = Exception.class)
    public void testLoadFlightsInvalidFileStucture() throws IOException {
        service.setFlightsFilename("/flights_invalid.csv");
        List<Flight> flights = service.loadFlightSchedule();
    }

    @Test(expected = RuntimeException.class)
    public void testBuildFlightScheduleFailure() throws IOException {
        when(flightScheduleBuildingService.buildFlilghtAssignments(any(List.class), any(List.class))).thenReturn(Optional.empty());

        service.buildFlightSchedule();
    }

    @Test
    public void testGetOperationPlanValid() {
       List<OperatingInstructionResource> plan =  service.getOperationsPlan("FL-0001");
       assertEquals(3, plan.size());
    }

    @Test
    public void testGetOperationPlanEmptySchedule() throws IOException {
        when(flightScheduleBuildingService.buildFlilghtAssignments(any(List.class), any(List.class))).thenReturn(Optional.of(Collections.emptyList()));
        service.buildFlightSchedule();

        assertTrue(service.getOperationsPlan("FL-0001").isEmpty());
    }

    @Test
    public void testGetOperationPlanNoFlightsFound() {
        assertTrue(service.getOperationsPlan("FL-0004").isEmpty());
    }

    @Test
    public void testGetFlightPlanForAllValid() {
        List<FlightAssignmentResource> plan =  service.getFlightPlan(null);
        assertEquals(4, plan.size());
    }

    @Test
    public void testGetFlightPlanForAllEmptySchedule() throws IOException {
        when(flightScheduleBuildingService.buildFlilghtAssignments(any(List.class), any(List.class))).thenReturn(Optional.of(Collections.emptyList()));
        service.buildFlightSchedule();

        assertTrue(service.getFlightPlan(null).isEmpty());
    }

    @Test
    public void testGetFlightPlanForAirportValid() {
        List<FlightAssignmentResource> plan =  service.getFlightPlan("MUC");
        assertEquals(2, plan.size());

        assertEquals(2, plan.stream().filter(r -> "MUC".equals(r.getOrigin())).count());
    }

    @Test
    public void testGetFlightPlanForAirportEmptySchedule() throws IOException {
        when(flightScheduleBuildingService.buildFlilghtAssignments(any(List.class), any(List.class))).thenReturn(Optional.of(Collections.emptyList()));
        service.buildFlightSchedule();

        assertTrue(service.getFlightPlan("TXL").isEmpty());
    }

    @Test
    public void testGetFlightPlanForAirportNoFlightsFound() {
        assertTrue(service.getFlightPlan("HAM").isEmpty());
    }

}

