package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

class DriverTest {
    private Driver numberOne;
    private final int ZONE = 5;
    private final String name = "Tyson";

    @BeforeEach
    public void setUp() {
        numberOne = new Driver(name, ZONE);
    }

    @Test
    public void testConstructor() {
        assertEquals(name, numberOne.getName());
        assertEquals(ZONE, numberOne.getZone());
        assertEquals(2.5, numberOne.getRanking());
        for (int i = 0; i < 24; i++) {
            assertEquals(5, numberOne.getAvailability(i));
        }
    }

    @Test
    public void testAddingWithinZoneRide() {
        int duration = 1;
        int time = 0;
        numberOne.changeAvailability(time, duration, ZONE);

        for (int i = time; i < time + duration ; i++) {
            assertEquals(0, numberOne.getAvailability(i));
        }
        for(int i = time + duration; i < 24 ; i++) {
            assertEquals(ZONE, numberOne.getAvailability(i));
        }
    }
    @Test
    public void testAddingAcrossZoneRide() {
        int end = 2;
        int duration = abs(end - ZONE) + 1;
        int time = 0;
        numberOne.changeAvailability(time, duration, end);

        for (int i = time; i < time + duration ; i++) {
            assertEquals(0, numberOne.getAvailability(i));
        }
        for(int i = time + duration; i < 24 ; i++) {
            assertEquals(end, numberOne.getAvailability(i));
        }
    }

    @Test
    public void testAddingMultipleRides() {
        int end = 2;
        int duration = abs(end - ZONE) + 1;
        int time = 5;
        numberOne.changeAvailability(time, duration, end);
        int end2 = 3;
        int duration2 = abs(end2 - ZONE) + 1;
        int time2 = 0;
        numberOne.changeAvailability(time2, duration2, end2);

        for (int i = time2; i < (time2 + duration2) ; i++) {
            assertEquals(0, numberOne.getAvailability(i));
        }
        for (int i = ((time2 + duration2 + 1)); i < time; i++) {
            assertEquals(end2, numberOne.getAvailability(i));
        }
        for (int i = time; i < time + duration ; i++) {
            assertEquals(0, numberOne.getAvailability(i));
        }
        for (int i = time + duration; i < 24 ; i++) {
            assertEquals(end, numberOne.getAvailability(i));
        }
    }

    @Test
    public void testMakeAvailableAgainAtMidnight() {
        int end = 2;
        int duration = abs(end - ZONE) + 1;
        int time = 0;
        numberOne.changeAvailability(time, duration, end);
        numberOne.availableAgain(time, duration);

        for (int i = time; i < time + duration; i++) {
            assertEquals(ZONE, numberOne.getAvailability(i));
        }
        for (int i = time + duration; i < 24; i++) {
            assertEquals(end, numberOne.getAvailability(i));
        }
    }

    @Test
    public void testMakeAvailableAgainAtAnytime() {
        int end = 2;
        int duration = abs(end - ZONE) + 1;
        int time = 5;
        numberOne.changeAvailability(time, duration, end);
        numberOne.availableAgain(time, duration);

        for (int i = time; i < time + duration; i++) {
            assertEquals(ZONE, numberOne.getAvailability(i));
        }
        for (int i = time + duration; i < 24; i++) {
            assertEquals(end, numberOne.getAvailability(i));
        }
    }

    @Test
    public void testChangeRanking() {
        numberOne.changeRanking(5);
        assertEquals((2.5 + 5) / 2 , numberOne.getRanking());
    }

    @Test
    public void testGetInformation() {
        String expected = name + " - " + numberOne.getRanking() + "/5";
        assertEquals(expected, numberOne.getInformation());
    }
}
