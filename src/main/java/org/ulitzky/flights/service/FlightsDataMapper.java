package org.ulitzky.flights.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.ulitzky.flights.api.v1.resource.FlightAssignmentResource;
import org.ulitzky.flights.api.v1.resource.OperatingInstructionResource;
import org.ulitzky.flights.model.FlightAssignment;

import java.util.List;

/**
 * Created by lulitzky on 19.04.18.
 */
@Mapper(componentModel = "spring")
public interface FlightsDataMapper {

    @Mappings({
            @Mapping(target = "origin", source = "assignment.flight.origin.code"),
            @Mapping(target = "destination", source = "assignment.flight.destination.code"),
            @Mapping(target = "departure", source = "assignment.flight.departureTime"),
            @Mapping(target = "arrival", source = "assignment.flight.scheduledArrivalTime"),
            @Mapping(target = "equipment", source = "assignment.aircraft.model")
    })
    FlightAssignmentResource mapToFlightAssignmentResource(final FlightAssignment assignment);
    List<FlightAssignmentResource> mapToFlightPlan(final List<FlightAssignment> assignments);

    @Mappings({
            @Mapping(target = "origin", source = "assignment.flight.origin.code"),
            @Mapping(target = "destination", source = "assignment.flight.destination.code"),
            @Mapping(target = "departure", source = "assignment.flight.departureTime")
    })
    OperatingInstructionResource mapToOperatingInstructionResource(final FlightAssignment assignment);
    List<OperatingInstructionResource> mapToOperatingInstructionResources(final List<FlightAssignment> assignments);
}
