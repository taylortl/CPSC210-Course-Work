package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

public class RideTest {
    Ride numberOne;
    int driver = 5;
    int start = 2;
    int end = 5;
    int time = 14;
    int oneZoneCost;
    int additionalCost;


    @BeforeEach
    public void setUp() {
        numberOne = new Ride(driver, start, end, time);
        oneZoneCost = numberOne.getOneZoneCost();
        additionalCost = numberOne.getAdditionalCost();
    }

    @Test
    public void testConstructor() {
        assertEquals(driver, numberOne.getDriver());
        assertEquals(start, numberOne.getStart());
        assertEquals(end, numberOne.getDestination());
        assertEquals(time, numberOne.getTime());
        assertEquals(start, numberOne.getStart());
        assertEquals((oneZoneCost + (end - start) * additionalCost), numberOne.getCost());
        assertTrue(numberOne.getReference() >= 0);
        assertFalse(numberOne.getAdditionalFee());
    }

    @Test
    public void testAddFee() {
        int times = 3;
        numberOne.addFee(times);
        int cost = (oneZoneCost + (end - start) * additionalCost) + additionalCost * times;
        assertEquals(cost, numberOne.getCost());
    }

    @Test
    public void testGetInformation() {
        String expected = "from zone " + start + " to zone " + end;
        assertEquals(expected, numberOne.getInformation());
    }
}
