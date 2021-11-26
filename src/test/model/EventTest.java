package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/******************************************
 *    Title: AlarmSystem
 *    Author: Paul Carter
 *    Date: 2021-10-21
 *    Location: https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
 *
 ******************************************/

public class EventTest {
	private Event event;
	private Date date;
    private String description;
    private final int HASH_CONSTANT = 13;
	
	@BeforeEach
	public void runBefore() {
        description = "Ride is being made";
		event = new Event(description);   // (1)
		date = Calendar.getInstance().getTime();   // (2)
	}
	
	@Test
	public void testEvent() {
		assertEquals(description, event.getDescription());
		assertEquals(date, event.getDate());
	}

    @Test
    public void testEqualEqual() {
        Event other = event;
        assertTrue(event.equals(other));
    }

    @Test
    public void testEqualDescriptionNotEqual() {
        description += " unsuccessfully";
        Event other = new Event(description);
        assertFalse(event.equals(other));
    }

    @Test
    public void testEqualWithNull() {
        assertFalse(event.equals(null));
    }

    @Test
    public void testEqualDateNotEqual() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            fail();
        }
        Event other = new Event(description);
        assertFalse(other.equals(event));
    }

    @Test
    public void testEqualWithDifferentObject() {
        date = Calendar.getInstance().getTime();
        assertFalse(event.equals(date));
    }

    @Test
    public void testHasCode() {
        int expectedHash = HASH_CONSTANT * date.hashCode() + description.hashCode();
        assertEquals(expectedHash, event.hashCode());
    }

	@Test
	public void testToString() {
		assertEquals(date.toString() + "\n" + description, event.toString());
	}
}
