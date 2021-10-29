package model;

import exceptions.DriversOffWork;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonTest {
    protected int time = 10;
    protected int start = 2;
    protected int end = 4;

    protected void checkCompany(Company company, int rideNumber, int time, int start, int duration, int expectedSize) throws DriversOffWork {
        assertTrue(company.numberOfDrivers() > 0);
        assertEquals(rideNumber, company.getUser().numberOfRides());
        List<String> driversList = company.getDriversOutOfZone(time, start, duration);
        assertEquals(expectedSize, driversList.size());
    }
}
