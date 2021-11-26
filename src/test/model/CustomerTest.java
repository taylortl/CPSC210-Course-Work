package model;

import exceptions.ReviewedRideException;
import exceptions.RideCannotBeCancelled;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    private Customer user;
    private String name = "Tyson";
    private int driver;
    private int start;
    private int end;
    private int time;
    int additional;
    private int withinZoneCost = 10;
    private int multiZonesCost = 5;

    @BeforeEach
    public void setUp() {
        user = new Customer();
        driver = 2;
        start = 2;
        end = 5;
        time = 14;
        additional = 0;
    }

    @Test
    public void testConstructor() {
        assertEquals(0,user.numberOfRides());
    }

    @Test
    public void testAddRideWithoutAdditionalFee() {
        int cost = user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        int expectedCost = withinZoneCost + (abs(start - end) + additional) * multiZonesCost;
        assertEquals(expectedCost, cost);
        assertTrue(user.numberOfRides() > 0);
        assertEquals(time, user.getTimeOfRide(0));
        assertEquals(driver, user.getDriverOfRide(0));
        assertEquals(start, user.getStartOfRide(0));
        assertEquals(end, user.getEndOfRide(0));
    }

    @Test
    public void testAddRideWithAdditionalFee() {
        driver = 4;
        additional = abs(driver - start);
        int cost = user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        int expectedCost = withinZoneCost + (abs(start - end) + additional) * multiZonesCost;
        assertEquals(expectedCost, cost);
        assertTrue(user.numberOfRides() > 0);
        assertEquals(time, user.getTimeOfRide(0));
        assertEquals(driver, user.getDriverOfRide(0));
        assertEquals(start, user.getStartOfRide(0));
        assertEquals(end, user.getEndOfRide(0));

    }

    @Test
    public void testReviewWithRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.changeReviewStateOfRide(0, 3);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
    }

    @Test
    public void testReviewWithReviewedRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.changeReviewStateOfRide(0, 3);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        try {
            user.changeReviewStateOfRide(0, 3);
            fail("ReviewedRideException should have occurred");
        } catch (ReviewedRideException e) {
            // correct
        }
    }

    @Test
    public void testRideHistoryWithoutRide() {
        List<String> history = user.getRideHistoryUnReviewed();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testRideHistoryWithRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        List<String> history = user.getRideHistoryUnReviewed();
        assertTrue(history.size() > 0);
    }

    @Test
    public void testRideHistoryWithReviewedRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.changeReviewStateOfRide(0, 3);
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        List<String> history = user.getRideHistoryUnReviewed();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testCancelReviewedRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.changeReviewStateOfRide(0, 3);
            user.cancel(0);
            fail("ReviewedRideException should have occurred");
        } catch (ReviewedRideException e) {
            // correct
        } catch (RideCannotBeCancelled e) {
            fail("There should be no RideCannotBeCancelled");
        }
    }

    @Test
    public void testCancelRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.cancel(0);
        } catch (RideCannotBeCancelled rideCannotBeCancelled) {
            fail("There should be no RideCannotBeCancelled");
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        assertEquals(0, user.numberOfRides());
    }

    @Test
    public void testCancelCrossZoneRide() {
        driver = start + 1;
        additional = abs(driver - start);
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        try {
            user.cancel(0);
            fail("RideCannotBeCancelled should have occurred");
        } catch (RideCannotBeCancelled rideCannotBeCancelled) {
           // correct
        } catch (ReviewedRideException e) {
            fail("There should be no ReviewedRideException");
        }
        assertEquals(1, user.numberOfRides());
    }

    @Test
    public void testTOJson() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        JSONObject json = user.toJson();
        JSONArray rides = json.getJSONArray("rides");
        assertFalse(rides.isEmpty());
    }
}
