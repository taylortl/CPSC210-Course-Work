package model;

import java.util.Calendar;
import java.util.Date;


/******************************************
 *    Title: AlarmSystem
 *    Author: Paul Carter
 *    Date: 2021-10-21
 *    Location: https://github.students.cs.ubc.ca/CPSC210/AlarmSystem
 *
 ******************************************/
//Represents a ride booking system event.
public class Event {
    private static final int HASH_CONSTANT = 13;
    private Date dateLogged;
    private String description;

    // EFFECTS: Creates an event with the given description and the current date/time stamp.
    public Event(String description) {
        dateLogged = Calendar.getInstance().getTime();
        this.description = description;
    }

    // Getters
    public Date getDate() {
        return dateLogged;
    }

    public String getDescription() {
        return description;
    }

    // EFFECTS: returns true if the given object has the same date and description as the current one
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other.getClass() != this.getClass()) {
            return false;
        }
        Event otherEvent = (Event) other;
        if (this.dateLogged.equals(otherEvent.dateLogged)) {
            return this.description.equals(otherEvent.description);
        } else {
            return false;
        }
    }

    // EFFECTS: return the hashCode of the current Event object
    @Override
    public int hashCode() {
        return (HASH_CONSTANT * dateLogged.hashCode() + description.hashCode());
    }

    // EFFECTS: return a description about the Event object
    @Override
    public String toString() {
        return dateLogged.toString() + "\n" + description;
    }
}
