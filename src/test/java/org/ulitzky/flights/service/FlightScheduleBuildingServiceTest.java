package org.ulitzky.flights.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.ulitzky.flights.model.AircraftLocation;
import org.ulitzky.flights.model.Flight;
import org.ulitzky.flights.model.FlightAssignment;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by lulitzky on 20.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlightScheduleBuildingServiceTest {

    @InjectMocks
    FlightScheduleBuildingService service;


    @Test
    public void testBuildScheduleNoFlights() throws IOException {
        List<AircraftLocation> homeBaseList = new LinkedList<>();
        homeBaseList.add(new AircraftLocation(CSVParser.parse("747-400,LHR,FL-0003", CSVFormat.DEFAULT).iterator().next()));
        homeBaseList.add(new AircraftLocation(CSVParser.parse("737,TXL,FL-0001", CSVFormat.DEFAULT).iterator().next()));

        assertTrue(service.buildFlilghtAssignments(homeBaseList, Collections.emptyList()).get().isEmpty());
    }


    @Test
    public void testBuildScheduleNoAircrafts() throws IOException {
        List<Flight> flightsList = new LinkedList<>();
        flightsList.add(new Flight(CSVParser.parse("10:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()));
        flightsList.add(new Flight(CSVParser.parse("12:00,TXL,MUC,01:00", CSVFormat.DEFAULT).iterator().next()));

        assertFalse(service.buildFlilghtAssignments( Collections.emptyList(), flightsList).isPresent());
    }

    @Test
    public void testBuildScheduleValid() throws IOException {
        List<Flight> flightsList = new LinkedList<>();
        flightsList.add(new Flight(CSVParser.parse("10:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()));
        flightsList.add(new Flight(CSVParser.parse("12:00,TXL,MUC,01:00", CSVFormat.DEFAULT).iterator().next()));

        List<AircraftLocation> homeBaseList = new LinkedList<>();
        homeBaseList.add(new AircraftLocation(CSVParser.parse("747-400,LHR,FL-0003", CSVFormat.DEFAULT).iterator().next()));
        homeBaseList.add(new AircraftLocation(CSVParser.parse("737,MUC,FL-0001", CSVFormat.DEFAULT).iterator().next()));

        List<FlightAssignment> schedule =  service.buildFlilghtAssignments(homeBaseList, flightsList).get();

        assertEquals(flightsList.size(), schedule.size());
    }

    @Test
    public void testBuildScheduleNoSolution() throws IOException {
        List<Flight> flightsList = new LinkedList<>();
        flightsList.add(new Flight(CSVParser.parse("10:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()));
        flightsList.add(new Flight(CSVParser.parse("12:00,TXL,MUC,01:00", CSVFormat.DEFAULT).iterator().next()));
        flightsList.add(new Flight(CSVParser.parse("12:00,HAM,MUC,02:00", CSVFormat.DEFAULT).iterator().next()));

        List<AircraftLocation> homeBaseList = new LinkedList<>();
        homeBaseList.add(new AircraftLocation(CSVParser.parse("747-400,LHR,FL-0003", CSVFormat.DEFAULT).iterator().next()));
        homeBaseList.add(new AircraftLocation(CSVParser.parse("737,MUC,FL-0001", CSVFormat.DEFAULT).iterator().next()));

        assertFalse(service.buildFlilghtAssignments(homeBaseList, flightsList).isPresent());
    }


    @Test
    public void testBuildScheduleNeedMove() throws IOException {
        List<Flight> flightsList = new LinkedList<>();
        flightsList.add(new Flight(CSVParser.parse("10:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()));
        flightsList.add(new Flight(CSVParser.parse("16:00,MUC,TXL,01:00", CSVFormat.DEFAULT).iterator().next()));

        List<AircraftLocation> homeBaseList = new LinkedList<>();
        homeBaseList.add(new AircraftLocation(CSVParser.parse("747-400,LHR,FL-0003", CSVFormat.DEFAULT).iterator().next()));
        homeBaseList.add(new AircraftLocation(CSVParser.parse("737,MUC,FL-0001", CSVFormat.DEFAULT).iterator().next()));

        List<FlightAssignment> schedule =  service.buildFlilghtAssignments(homeBaseList, flightsList).get();

        assertEquals(flightsList.size() + 1, schedule.size()); // The is one not-scheduled aircraft move from TXL to MUC added
    }

}
