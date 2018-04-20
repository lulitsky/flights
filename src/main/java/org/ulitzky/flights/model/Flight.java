package org.ulitzky.flights.model;

import org.apache.commons.csv.CSVRecord;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lulitzky on 19.04.18.
 */
public class Flight {

    @NotNull
    private final Airport origin;

    @NotNull
    private final Airport destination;

    @NotNull
    private final  Date departureTime;

    @NotNull
    private final int flightLength;

    public Flight(final Airport origin, final Airport destination, final Date departureTime, final int flightLength) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.flightLength = flightLength;
    }

    public Flight(final CSVRecord record) {
        // sample format : 10:00,TXL,MUC,01:00

        if (record.size() < 4) {
            throw new IllegalArgumentException("Invalid aircraft location record " + record);
        }

        this.origin = new Airport(record.get(1));
        this.destination = new Airport(record.get(2));


        this.departureTime = parseDepartureTime(record.get(0));

        this.flightLength = parseFlightLength(record.get(3));

    }

    static Date parseDepartureTime(final String time) {
        validateTimeInput(time);
        int hours = Integer.valueOf(time.substring(0, 2));
        int minutes =  Integer.valueOf(time.substring(3, 5));

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    static int parseFlightLength(final String time) {
        validateTimeInput(time);
        int hours = Integer.valueOf(time.substring(0, 2));
        int minutes =  Integer.valueOf(time.substring(3, 5));
        return hours * 60 + minutes;
    }

    private static void validateTimeInput(final String time) {
        if ((time.length() != 5) || (time.charAt(2) != ':')) {
            throw new IllegalArgumentException("Invalid time format " + time);
        }
    }

    public Airport getOrigin() {
        return origin;
    }


    public Airport getDestination() {
        return destination;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public int getFlightLength() {
        return flightLength;
    }

    public Date getScheduledArrivalTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(departureTime);
        cal.add(Calendar.MINUTE, flightLength);
        return cal.getTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(origin).append(" to ").append(destination).append(" at ").append(departureTime).append("; length: ").append(flightLength);
        return sb.toString();
    }
}
