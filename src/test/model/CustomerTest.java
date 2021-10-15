package model;

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
    public void testReviewWithoutRide() {
        assertFalse(user.changeReviewStateOfRide(0));

    }

    @Test
    public void testReviewWithRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        assertTrue(user.changeReviewStateOfRide(0));
    }

    @Test
    public void testReviewWithReviewedRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        user.changeReviewStateOfRide(0);
        assertFalse(user.changeReviewStateOfRide(0));
    }

    @Test
    public void testRideHistoryWithoutRide() {
        List<String> history = user.getRideHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testRideHistoryWithRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        List<String> history = user.getRideHistory();
        assertTrue(history.size() > 0);
    }

    @Test
    public void testCancellableWithoutRide() {
        assertEquals(-1, user.cancellable(0));
    }

    @Test
    public void testCancellableWithRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
        assertEquals(driver, user.cancellable(0));
    }

    @Test
    public void testCancelRide() {
        user.addRide(time, start, end, driver, additional, name, withinZoneCost, multiZonesCost);
//        assertTrue(user.numberOfRides() > 0);
        assertEquals(1, user.numberOfRides());
        user.cancel(0);
        assertEquals(0, user.numberOfRides());
    }
}
