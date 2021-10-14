package model;

import java.util.Arrays;

public class Driver {
    private static int driverNum = 0;

    private String name; // name of the driver
    private int id; // number corresponding to the driver
    private int zone;
    private double ranking; // ranking of the driver
    private int numberOfCustomers = 0;
    /* an array of numbers representing the location of the driver at that time
        eq: unavailable at 1pm -> availability[13] = 0
            available at 2pm in zone 3 -> availability[14] = 3
     */
    private int[] availability;

    /*
        REQUIRES: 1 <= zone <= 5
        EFFECTS: initialize all the data members,
                 set this driver to be available all the time at his/her given location,
                 set the ranking of this driver to be half of 2.5,
                 give a number to this driver.
     */
    public Driver(String name, int zone) {
        id = driverNum++;
        this.name = name;
        this.zone = zone;
        availability = new int[24];
        Arrays.fill(availability, zone);
        ranking = 2.5;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getRanking() {
        return ranking;
    }

    public String getName() {
        return name;
    }

    /*
        REQUIRES: 0 <= time <= 23
        EFFECTS: returns 0 if the driver is not available at the given time.
                 Else, returns the location (zone) of the driver at the given time.
     */
    public int getAvailability(int time) {
        return (availability[time]);
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= duration <= 4, 1 <= destination <= 5
        MODIFIES: this
        EFFECTS: driver becomes unavailable during a ride,
                 and the driver's location is changed to the destination after the ride.
     */
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

    /*
        REQUIRES: 0 <= time <= 23, 1 <= duration <= 4
        MODIFIES: this
     */
    public void availableAgain(int time, int duration) {
        if (time == 0) {
            Arrays.fill(availability, time, (time + duration), zone);
        } else {
            Arrays.fill(availability, time, (time + duration), availability[time - 1]);
        }
    }

    /*
        REQUIRES: 0 <= newRank <= 5
        MODIFIES: this
        EFFECTS: update the ranking of this driver.
     */
    public void changeRanking(double newRank) {
        ranking = (ranking + newRank) / 2;
    }

    // EFFECTS: returns a string telling the number, name and ranking of the driver.
    public String getInformation() {
        return (id + ": " + name + " - " + ranking + "/5");
    }

}
