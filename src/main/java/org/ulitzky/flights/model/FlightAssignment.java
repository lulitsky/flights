package org.ulitzky.flights.model;

import javax.validation.constraints.NotNull;

/**
 * Created by lulitzky on 19.04.18.
 */
public class FlightAssignment {

    @NotNull
    private final Flight flight;

    private Aircraft aircraft;

    public FlightAssignment(final Flight flight, final Aircraft aircraft) {
        this.flight = flight;
        this.aircraft = aircraft;
    }

    public Flight getFlight() {
        return flight;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(final Aircraft aircraft) {
        this.aircraft = aircraft;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(flight).append(" operated by ").append(aircraft);
        return sb.toString();
    }

}
