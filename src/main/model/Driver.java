package model;

import java.util.Arrays;

// Represents a driver with his/her rating initially set as 2.5/
public class Driver {

    private String name;
    private int zone;
    private double rating;

    /* an array of numbers representing the location of the driver at that time
        eq: unavailable at 1pm -> availability[13] = 0
            available at 2pm in zone 3 -> availability[14] = 3
     */
    private int[] availability;

    /*
        REQUIRES: 1 <= zone <= 5
        EFFECTS: initializes all the data members,
                 sets this driver to be available all the time at his/her given location,
                 sets the rating of this driver to be half of 2.5,
                 gives a number to this driver.
     */
    public Driver(String name, int zone) {
        this.name = name;
        this.zone = zone;
        availability = new int[24];
        Arrays.fill(availability, zone);
        rating = 2.5;
    }

    // Getters
    public double getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public int getZone() {
        return zone;
    }

    // REQUIRES: 0 <= time <= 23
    // EFFECTS: returns 0 if the driver is not available at the given time.
    //          Else, returns the location (zone) of the driver at the given time.
    public int getAvailability(int time) {
        return (availability[time]);
    }

    // REQUIRES: 0 <= time <= 22, 1 <= destination <= 5
    // MODIFIES: this
    // EFFECTS: driver becomes unavailable during a ride,
    //          and the driver's location is changed to the destination after the ride.
    public void changeAvailability(int time, int duration, int destination) {
        Arrays.fill(availability, time, (time + duration), 0);
        for (int i = time + duration; i < availability.length; i++) {
            if (availability[i] == 0) {
                break;
            } else {
                availability[i] = destination;
            }
        }
    }

    // REQUIRES: 0 <= time <= 23, 1 <= duration <= 4
    // MODIFIES: this
    // EFFECTS: the driver will be available at the given time for the given duration
    public void availableAgain(int time, int duration) {
        if (time == 0) {
            Arrays.fill(availability, time, (time + duration), zone);
        } else {
            Arrays.fill(availability, time, (time + duration), availability[time - 1]);
        }
    }

    // REQUIRES: 0 <= newRate <= 5
    // MODIFIES: this
    // EFFECTS: updates the rating of this driver.
    public void changeRating(double newRate) {
        rating = (rating + newRate) / 2;
    }

    // EFFECTS: returns a string telling the name and rating of the driver.
    public String getInformation() {
        return (name + " - " + rating + "/5.0");
    }

}
