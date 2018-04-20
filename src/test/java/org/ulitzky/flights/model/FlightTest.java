package org.ulitzky.flights.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.assertj.core.util.DateUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by lulitzky on 19.04.18.
 */
public class FlightTest {

    @Test
    public void testParseDepartureTimeValid() {
        Date parsedDate = Flight.parseDepartureTime("02:30");
        Date now = new Date();

        Calendar parsedCal = Calendar.getInstance();
        parsedCal.setTime(parsedDate);
        assertEquals(2, parsedCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, parsedCal.get(Calendar.MINUTE));

        assertEquals(DateUtil.truncateTime(now), DateUtil.truncateTime(parsedDate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDepartureTimeInvalidFormat() {
        Flight.parseDepartureTime("12345");
    }

    @Test(expected = Exception.class)
    public void testParseDepartureTimeInvalidNumber() {
        Flight.parseDepartureTime("AB:11");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDepartureTimeInvalidStringLength() {
        Flight.parseDepartureTime("1:00");
    }

    @Test
    public void testParseFlightLengthValidLongFlight() {
        assertEquals(150, Flight.parseFlightLength("02:30"));
    }

    @Test
    public void testParseFlightLengthValidShortFlight() {
        assertEquals(45, Flight.parseFlightLength("00:45"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testParseFlightLengthInvalidFormat() {
        Flight.parseFlightLength("12345");
    }

    @Test(expected = Exception.class)
    public void testParseFlightLengthInvalidNumber() {
        Flight.parseFlightLength("AB:11");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFlightLengthInvalidStringLength() {
        Flight.parseFlightLength("1:00");
    }

    @Test
    public void testCSVRecordConstructorValid() throws IOException {
        CSVRecord record = CSVParser.parse("20:30,LHR,MUC,02:00", CSVFormat.DEFAULT).iterator().next();
        Flight flight = new Flight(record);

        assertEquals("LHR", flight.getOrigin().getCode());
        assertEquals("MUC", flight.getDestination().getCode());
        assertEquals(120, flight.getFlightLength());

        Date now = new Date();

        Calendar departureCal = Calendar.getInstance();
        departureCal.setTime(flight.getDepartureTime());

        assertEquals(DateUtil.truncateTime(now), DateUtil.truncateTime(departureCal.getTime()));
        assertEquals(20, departureCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, departureCal.get(Calendar.MINUTE));


        Calendar arrivalCal = Calendar.getInstance();
        arrivalCal.setTime(flight.getScheduledArrivalTime());

        assertEquals(DateUtil.truncateTime(now), DateUtil.truncateTime(arrivalCal.getTime()));
        assertEquals(22, arrivalCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, arrivalCal.get(Calendar.MINUTE));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCSVRecordConstructorInvalidRecordStructur() throws IOException {
        CSVRecord record = CSVParser.parse("20:30,LHR,MUC", CSVFormat.DEFAULT).iterator().next();
        Flight flight = new Flight(record);
    }

}
