package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static org.junit.jupiter.api.Assertions.*;

public class CompanyTest {
    private Company ourCompany;
    private Customer customer;
    private int time;
    private int start;
    private int end;
    private int duration;
    private int driver;
    private int additional;
    private String name = "Dyson";
    private int withinZoneCost;
    private int multiZonesCost;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        ourCompany = new Company(customer);
        time = 10;
        start = 2;
        end = 4;
        duration = abs(end - start);
        driver = 3;
        additional = abs(ourCompany.getDriverZone(driver) - start);
        withinZoneCost = ourCompany.getOneZoneCost();
        multiZonesCost = ourCompany.getAdditionalFee();
    }

    @Test
    public void testConstructor() {
        assertTrue(ourCompany.numberOfDrivers() > 0);
        for (int i = 0 ; i < ourCompany.numberOfDrivers(); i++) {
            assertTrue(ourCompany.getDriverZone(i) > 0);
            assertTrue(ourCompany.getDriverZone(i) < 6);
        }
    }

    @Test
    public void testAddRide() {
        int cost = ourCompany.addRide(time, start, end, driver, additional);
        int expectedCost = withinZoneCost + (abs(start - end) + additional) * multiZonesCost;
        assertEquals(expectedCost, cost);
    }

    @Test
    public void testWriteReviewWithRide() {
        ourCompany.addRide(time, start, end, driver, additional);
        int reference = customer.numberOfRides() - 1;
        int driver = customer.getDriverOfRide(reference);
        double ranking = 4.25;
        assertTrue(ourCompany.writeReview(reference, ranking, driver));
    }

    @Test
    public void testWriteReviewWithoutRide() {
        assertFalse(ourCompany.writeReview(0, 4.5, driver));
    }

    @Test
    public void testGetDriversWithinZonWithoutRides() {
        List<String> driversAvailable;
        for (int i = 1; i < 6; i++) {
            driversAvailable= ourCompany.getDriversWithinZone(time, i, duration);
            assertTrue(driversAvailable.size() > 0);
        }
    }

    @Test
    public void testGetDriversWithinZoneWithRidesInBetweenDuration() {
        for (int i = 0 ; i < ourCompany.numberOfDrivers(); i++) {
            ourCompany.addRide(time + 1, start, end, i, additional);
        }
        List<String> driversAvailable= ourCompany.getDriversWithinZone(time, start, duration);
        assertEquals(0,driversAvailable.size());
    }

    @Test
    public void testGetDriversWithinZonWithRides() {
        for (int i = 0 ; i < ourCompany.numberOfDrivers(); i++) {
            ourCompany.addRide(time, start, end, i, additional);
        }
        List<String> driversAvailable= ourCompany.getDriversWithinZone(time, start, duration);
        assertEquals(0,driversAvailable.size());
    }

    @Test
    public void testGetDriversOutOfZoneWithoutRides() {
        List<String> driversAvailable = ourCompany.getDriversOutOfZone(time, start, duration);
        assertTrue(driversAvailable.size() > 0);
    }
    @Test
    public void testGetDriversOutOfZoneWithRidesInBetweenDuration() {
        for (int i = 0 ; i < ourCompany.numberOfDrivers(); i++) {
            ourCompany.addRide(time + 1, start , end, i, additional);
        }
        List<String> driversAvailable = ourCompany.getDriversOutOfZone(time, start, duration);
        assertEquals(0, driversAvailable.size());
    }

    @Test
    public void testGetDriversOutOfZoneWithRides() {
        for (int i = 0 ; i < ourCompany.numberOfDrivers(); i++) {
            ourCompany.addRide(time, start , end, i, additional);
        }
        List<String> driversAvailable = ourCompany.getDriversOutOfZone(time, start, duration);
        assertEquals(0, driversAvailable.size());
    }

    @Test
    public void testGetAddedFeeDifferentZone() {
        int fee = ourCompany.getAddedFee(driver, start);
        int expected = additional * multiZonesCost;
        assertEquals(expected, fee);
    }

    @Test
    public void testGetAddedFeeSameZone() {
        int driver = 0;
        int startZone = ourCompany.getDriverZone(0);
        int fee = ourCompany.getAddedFee(driver, startZone);
        int expected = 0;
        assertEquals(expected, fee);
    }

    @Test
    public void testCancellation() {
        ourCompany.addRide(time, start, end, driver, additional);
        ourCompany.cancellation(driver, time, duration, customer.numberOfRides() - 1);

        assertEquals(0, customer.numberOfRides());
        int bookAgain = ourCompany.addRide(time, start, end, driver, additional);
        assertTrue(bookAgain > 0);
    }

    @Test
    public void getDriverZoneWithRealDriver() {
        int zone = ourCompany.getDriverZone(0);
        assertTrue(zone > 0);
    }

    @Test
    public void getDriverZoneWithFakeDriver() {
        int zone = ourCompany.getDriverZone(ourCompany.numberOfDrivers());
        assertEquals(0, zone);
    }
}
