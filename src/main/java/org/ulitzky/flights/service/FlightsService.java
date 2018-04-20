package org.ulitzky.flights.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ulitzky.flights.api.v1.resource.FlightAssignmentResource;
import org.ulitzky.flights.api.v1.resource.OperatingInstructionResource;
import org.ulitzky.flights.model.AircraftLocation;
import org.ulitzky.flights.model.Flight;
import org.ulitzky.flights.model.FlightAssignment;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by lulitzky on 19.04.18.
 */
@Service
public class FlightsService {

   private List<FlightAssignment> flightSchedule;

    @Value("${input.homebase:/homebases.csv}")
    private String homebaseFilename;

    @Value("${input.flights:/flights.csv}")
    private String flightsFilename;

   @Autowired
   private FlightsDataMapper dataMapper;

   @Autowired
   private FlightScheduleBuildingService schedulerBuilder;

    @PostConstruct
    public void buildFlightSchedule() throws IOException {
        Optional<List<FlightAssignment>> scheduleOption = schedulerBuilder.buildFlilghtAssignments(loadHomeBase(), loadFlightSchedule());

        if (scheduleOption.isPresent()) {
            flightSchedule = scheduleOption.get();
        } else {
            throw new RuntimeException("Cannot build valid schedule");
        }
    }



    List<AircraftLocation> loadHomeBase() throws IOException {
        InputStream dataInputStream = getClass().getResourceAsStream(homebaseFilename);
        CSVParser parser = CSVParser.parse(dataInputStream, Charset.defaultCharset(), CSVFormat.DEFAULT);

        List<AircraftLocation> homeBaseList = parser.getRecords().stream().map(r -> new AircraftLocation(r)).collect(Collectors.toList());

        return homeBaseList;
    }

     List<Flight> loadFlightSchedule() throws IOException {
        InputStream dataInputStream = getClass().getResourceAsStream(flightsFilename);
        CSVParser parser = CSVParser.parse(dataInputStream, Charset.defaultCharset(), CSVFormat.DEFAULT);

        List<Flight> flightsList = parser.getRecords().stream().map(r -> new Flight(r)).collect(Collectors.toList());

        return flightsList;
    }

    public List<FlightAssignmentResource> getFlightPlan(final String origin) {
        List<FlightAssignment> filteredSchedule = new LinkedList<>();
        if (origin == null) {
            filteredSchedule.addAll(flightSchedule);
        } else {
            filteredSchedule = flightSchedule.stream().filter( item -> origin.equalsIgnoreCase(item.getFlight().getOrigin().getCode()) ).collect(Collectors.toList());
        }
        return  dataMapper.mapToFlightPlan(filteredSchedule);
    }

    public List<OperatingInstructionResource> getOperationsPlan(final @NotNull String registration) {
        List<FlightAssignment> flightsForAircraft = flightSchedule.stream().filter( item -> registration.equals(item.getAircraft().getRegistration())).collect(Collectors.toList());
        return dataMapper.mapToOperatingInstructionResources(flightsForAircraft);
    }


    void setHomebaseFilename(final String homebaseFilename) {
        this.homebaseFilename = homebaseFilename;
    }

    void setFlightsFilename(final String flightsFilename) {
        this.flightsFilename = flightsFilename;
    }

    List<FlightAssignment> getFlightSchedule() {
        return flightSchedule;
    }
}
