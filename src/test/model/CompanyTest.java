package model;

import exceptions.OutOfBoundInput;
import exceptions.ReviewedRideException;
import exceptions.RideCannotBeCancelled;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Math.abs;
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
    private int withinZoneCost;
    private int multiZonesCost;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        ourCompany = new Company(customer);
        time = 10;
        start = 2;
        end = 4;
        driver = 3;
        duration = abs(end - start);
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
        assertEquals(customer, ourCompany.getUser());
    }

    @Test
    public void testAddRide() {
        int cost = ourCompany.addRide(time, start, end, driver, additional);
        int expectedCost = withinZoneCost + (abs(start - end) + additional) * multiZonesCost;
        assertEquals(expectedCost, cost);
    }

    @Test
    public void testWriteReviewWithRide() {
        double ranking = 4.25;
        ourCompany.addRide(time, start, end, driver, additional);
        try {
            ourCompany.rateDriver(0, ranking, driver);
            customer.getDriverOfRide(0);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
    }

    @Test
    public void testWriteReviewWithReviewedRide() {
        double ranking = 4.25;
        ourCompany.addRide(time, start, end, driver, additional);
        try {
            ourCompany.rateDriver(0, ranking, driver);
            customer.getDriverOfRide(0);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        try {
            ourCompany.rateDriver(0, ranking, driver);
            customer.getDriverOfRide(0);
            fail("ReviewedRideException should have occurred");
        } catch (ReviewedRideException e) {
            // correct
        }
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
        List<String> driversAvailable = ourCompany.getDriversWithinZone(time, start, duration);
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
    public void testCancellationReviewedRide() {
        // add a ride
        ourCompany.addRide(time, start, end, start, additional);
        // review the ride
        try {
            ourCompany.rateDriver(0, 4, driver);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        // try to cancel it
        try {
            ourCompany.cancellation(start, time, duration, customer.numberOfRides() - 1);
            fail("ReviewedRideException should have occurred");
        } catch (ReviewedRideException e) {
            // correct
        } catch (RideCannotBeCancelled e) {
            fail("There should be no RideCannotBeCancelled");
        }
    }

    @Test
    public void testCancellationCrossZoneRide() {
        // add a ride
        driver = start + 1;
        additional = abs(driver - start);
        ourCompany.addRide(time, start, end, driver, additional);

        // try to cancel it
        try {
            ourCompany.cancellation(driver, time, duration, customer.numberOfRides() - 1);
            fail("RideCannotBeCancelled should have occurred");
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        } catch (RideCannotBeCancelled e) {
            // correct
        }
    }

    @Test
    public void testCancellationNormalRide() {
        // add a ride
        ourCompany.addRide(time, start, end, start, additional);
        // review the ride
        try {
            ourCompany.cancellation(start, time, duration, customer.numberOfRides() - 1);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        } catch (RideCannotBeCancelled e) {
            fail("There should be no RideCannotBeCancelled");
        }
    }

    @Test
    public void testToJson() {
        JSONObject json = ourCompany.toJson();
        JSONObject customerJson = json.getJSONObject("customer");
        assertTrue(customerJson.getJSONArray("rides").isEmpty());
    }
}
