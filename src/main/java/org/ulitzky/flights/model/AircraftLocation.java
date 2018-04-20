package org.ulitzky.flights.model;

import org.apache.commons.csv.CSVRecord;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by lulitzky on 19.04.18.
 */
public class AircraftLocation {

    @NotNull
    private Aircraft aircraft;

    @NotNull
    private Airport airport;

    private Date time;

    public AircraftLocation(final Aircraft aircraft, final Airport airport, final Date time) {
        this.aircraft = aircraft;
        this.airport = airport;
        this.time = time;
    }

    public AircraftLocation(final CSVRecord record) {
        // sample format : 737,TXL,FL-0001

        if (record.size() < 3) {
            throw new IllegalArgumentException("Invalid aircraft location record " + record);
        }
        airport = new Airport(record.get(1));
        aircraft = new Aircraft(record.get(2), record.get(0));

    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(final Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(final Airport airport) {
        this.airport = airport;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(aircraft).append(" at ").append(airport);
        if (time != null) {
            sb.append(" ").append(time);
        }

        return sb.toString();
    }
}
