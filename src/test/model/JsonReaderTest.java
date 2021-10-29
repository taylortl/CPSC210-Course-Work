package model;

import exceptions.DriversOffWork;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.IOException;
import java.util.List;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
public class JsonReaderTest extends JsonTest {
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/NonExistent.json");
        try {
            Company company = reader.read();
            fail("An IOException should have occurred");
        } catch (IOException e) {
            // correct
        }
    }

    @Test
    void testReaderEmptyCompany() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyCompany.json");
        try {
            Company loadedCompany = reader.read();
            checkCompany(loadedCompany, 0, 0, 1, 22, loadedCompany.numberOfDrivers());
        } catch (IOException e) {
            fail("There should be no IOException");
        } catch (DriversOffWork driversOffWork) {
            fail("There should be no DriversOffWork Exception");
        }
    }

    @Test
    void testReaderGeneralCompany() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralCompany.json");
        try {
            Company loadedCompany = reader.read();
            checkCompany(loadedCompany, 1, time, start, abs(start - end), loadedCompany.numberOfDrivers() - 1);
            List<String> driversAvailable = loadedCompany.getDriversWithinZone(time, start, abs(start - end));
            assertEquals(0, driversAvailable.size());
        } catch (IOException e) {
            fail("There should be no IOException");
        } catch (DriversOffWork driversOffWork) {
            fail("There should be no DriversOffWork Exception");
        }
    }
}
