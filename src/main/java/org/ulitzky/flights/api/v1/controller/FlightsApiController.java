package org.ulitzky.flights.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ulitzky.flights.service.FlightsService;
import org.ulitzky.flights.api.v1.resource.FlightAssignmentResource;
import org.ulitzky.flights.api.v1.resource.OperatingInstructionResource;

import java.util.List;

/**
 * Created by lulitzky on 19.04.18.
 */
@RestController
@RequestMapping(value = "")
@Slf4j
public class FlightsApiController {

    @Autowired
    private FlightsService flightsService;

    @RequestMapping(method = RequestMethod.GET, value = "/flightplan")
    public List<FlightAssignmentResource> getFlightPlan(@RequestParam(name = "airport", required=false)String origin) {
        log.info("Getting flightplan for origin airport {}", origin);
        return flightsService.getFlightPlan(origin);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/operationsplan")
    public List<OperatingInstructionResource> getOperationsPlan(@RequestParam(name = "registration", required=true)String registration){
        log.info("Getting operations plan by registration {}", registration);
        return flightsService.getOperationsPlan(registration);
    }

}
