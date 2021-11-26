package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/******************************************
 *    Title: AlarmSystem
 *    Author: Paul Carter
 *    Date: 2021-10-21
 *    Location: https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
 *
 ******************************************/
// Represents a log of ride booking system events
public class EventLog implements Iterable<Event> {
    private static EventLog theLog;
    private Collection<Event> events;

    // EFFECTS: create the only event log in the application with an empty list of events
    private EventLog() {
        events = new ArrayList<>();
    }

    // EFFECTS: return the only event log in the application, create one if not created.
    public static EventLog getInstance() {
        if (theLog == null) {
            theLog = new EventLog();
        }
        return theLog;
    }

    // EFFECTS: add an event to the list of events
    public void logEvent(Event e) {
        events.add(e);
    }

    // EFFECTS: clear all the events in the log and add an event about the clearing
    public void clear() {
        events.clear();
        logEvent(new Event("Event log cleared."));
    }

    // EFFECTS: return an iterator of the list of events
    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }
}

