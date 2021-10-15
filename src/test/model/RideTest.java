package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

public class RideTest {
    private Ride numberOne;
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
    }

    @Test
    public void testConstructor() {
        assertEquals(driver, numberOne.getDriver());
        assertEquals(name, numberOne.getDriverName());
        assertEquals(start, numberOne.getStart());
        assertEquals(end, numberOne.getDestination());
        assertEquals(time, numberOne.getTime());
        assertEquals(start, numberOne.getStart());
        assertEquals((withinZoneCost + (end - start) * multiZonesCost), numberOne.getTotalCost());
        assertFalse(numberOne.isReviewed());
        assertFalse(numberOne.getOtherZoneDriver());
    }

    @Test
    public void testAddFee() {
        int times = 3;
        numberOne.addFee(times);
        int cost = (withinZoneCost + (end - start) * multiZonesCost + multiZonesCost * times);
        assertEquals(cost, numberOne.getTotalCost());
        assertTrue(numberOne.getOtherZoneDriver());
    }

    @Test
    public void testGetInformation() {
        String expected = "from zone " + start + " to zone " + end;
        assertEquals(expected, numberOne.getInformation());
    }

    @Test
    public void testReview() {
        numberOne.setReviewed();
        assertTrue(numberOne.isReviewed());
    }
}
