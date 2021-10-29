package model;

import exceptions.DriversOffWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.util.List;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
public class JsonWriterTest extends JsonTest {
    private Customer ourCustomer;
    private Company ourCompany;
    private int driver;


    @BeforeEach
    public void setUp() {
        ourCustomer = new Customer();
        ourCompany = new Company(ourCustomer);
        for (int i = 0 ; i < ourCompany.numberOfDrivers() ; i++) {
            if (ourCompany.getDriverZone(i) == start) {
                driver = i;
            }
        }
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("An IOException should have occurred");
        } catch (IOException e) {
            // correct
        }
    }

    @Test
    void testWriterEmptyCompany() {
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyCompany.json");
            writer.open();
            writer.write(ourCompany);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyCompany.json");
            Company loadedCompany = reader.read();
            checkCompany(loadedCompany, 0, 0, 1, 22, loadedCompany.numberOfDrivers());
        } catch (IOException e) {
            fail("There should be no IOException");
        } catch (DriversOffWork driversOffWork) {
            fail("There should be no DriversOffWork Exception");
        }
    }

    @Test
    void testWriterGeneralCompany() {

        try {
            ourCompany.addRide(time, start, end, driver, 0);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralCompany.json");
            writer.open();
            writer.write(ourCompany);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralCompany.json");
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
