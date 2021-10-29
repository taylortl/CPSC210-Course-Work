package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RideTest {
    private Ride numberOne;
    private Ride numberTwo;
    private int driver = 5;
    private String name = "Tyson";
    private int start = 2;
    private int end = 5;
    private int time = 14;
    private int withinZoneCost = 10;
    private int multiZonesCost = 5;

    @BeforeEach
    public void setUp() {
        numberOne = new Ride(driver, name, start, end, time, withinZoneCost, multiZonesCost);
        numberTwo = new Ride(driver, name, start, end, time, withinZoneCost, multiZonesCost, true);
    }

    @Test
    public void testConstructor1() {
        assertEquals(driver, numberOne.getDriver());
        assertEquals(name, numberOne.getDriverName());
        assertEquals(start, numberOne.getStart());
        assertEquals(end, numberOne.getDestination());
        assertEquals(time, numberOne.getTime());
        assertEquals(start, numberOne.getStart());
        assertEquals((withinZoneCost + (end - start) * multiZonesCost), numberOne.getTotalCost());
        assertFalse(numberOne.isReviewed());
        assertEquals(0, numberOne.getOtherZoneDriver());
    }

    @Test
    public void testConstructor2() {
        assertEquals(driver, numberTwo.getDriver());
        assertEquals(name, numberTwo.getDriverName());
        assertEquals(start, numberTwo.getStart());
        assertEquals(end, numberTwo.getDestination());
        assertEquals(time, numberTwo.getTime());
        assertEquals(start, numberTwo.getStart());
        assertEquals((withinZoneCost + (end - start) * multiZonesCost), numberTwo.getTotalCost());
        assertTrue(numberTwo.isReviewed());
        assertEquals(0, numberTwo.getOtherZoneDriver());
    }

    @Test
    public void testAddFee() {
        int times = 3;
        numberOne.addFee(times);
        int cost = (withinZoneCost + (end - start) * multiZonesCost + multiZonesCost * times);
        assertEquals(cost, numberOne.getTotalCost());
        assertEquals(times, numberOne.getOtherZoneDriver());
    }

    @Test
    public void testGetInformation() {
        String expected = "from zone " + start + " to zone " + end + " at " + time + ":00";
        assertEquals(expected, numberOne.getInformation());
    }

    @Test
    public void testReview() {
        numberOne.setReviewed();
        assertTrue(numberOne.isReviewed());
    }

    @Test
    public void testToJson() {
        JSONObject json = numberOne.toJson();
        assertEquals(driver, json.getInt("driver"));
        assertEquals(time, json.getInt("startTime"));
        assertEquals(start, json.getInt("startZone"));
        assertEquals(end, json.getInt("destination"));
        assertEquals(0, json.getInt("additionalFee"));
    }
}
