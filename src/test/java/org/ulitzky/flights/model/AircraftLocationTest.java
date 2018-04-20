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
public class AircraftLocationTest {
    @Test
    public void testCSVRecordConstructorValid() throws IOException {
        CSVRecord record = CSVParser.parse("A320,HAM,FL-0004", CSVFormat.DEFAULT).iterator().next();
        AircraftLocation location = new AircraftLocation(record);

        assertEquals("HAM", location.getAirport().getCode());
        assertEquals( "A320", location.getAircraft().getModel());
        assertEquals("FL-0004", location.getAircraft().getRegistration());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCSVRecordConstructorInvalidRecordStructure() throws IOException {
        CSVRecord record = CSVParser.parse("A320,HAM", CSVFormat.DEFAULT).iterator().next();
        AircraftLocation location = new AircraftLocation(record);
    }

}
