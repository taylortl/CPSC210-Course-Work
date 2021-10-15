package model;

import static java.lang.Math.abs;

/*
    Contains information of a ride like, the driver, starting time, original zone and destination zone.
    The cost is calculated with zones and each ride is assigned to a reference number.
 */
public class Ride {
    private int driver; // driver of the ride (represented in his/her number)
    private String driverName;
    private int time; // starting time
    private int start; // zone of the starting position
    private int destination; // zone of the destination
    private int totalCost; // cost of the ride
    private int withinZoneFee;
    private int additionalFee;
    private boolean otherZoneDriver; // true if the ride calls a driver from other zones
    private boolean reviewed;



    /*  REQUIRES: 0 <= driverNumber < the number of drivers in the list, 1 <= startZone <  5, 1 <= desZone <  5
        EFFECTS: initialize all the data member, calculate the cost of this ride,
                 assume that the ride doesn't cost any additional fee, create a reference number for this ride.
    */
    public Ride(int driverNumber, String driverName, int startZone, int desZone, int time, int oneZone, int multiZone) {
        driver = driverNumber;
        this.driverName = driverName;
        start = startZone;
        destination = desZone;
        this.time = time;
        withinZoneFee = oneZone;
        additionalFee = multiZone;
        totalCost = withinZoneFee + abs(start - destination) * additionalFee;
        otherZoneDriver = false;
        reviewed = false;
    }

    // Getters
    public int getDriver() {
        return driver;
    }

    public String getDriverName() {
        return driverName;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTime() {
        return time;
    }

    public int getStart() {
        return start;
    }

    public int getDestination() {
        return destination;
    }

    public boolean getOtherZoneDriver() {
        return otherZoneDriver;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed() {
        this.reviewed = true;
    }

    // MODIFIES: this
    // EFFECTS: add the additional fee if the user chooses a driver from a further zone.
    public void addFee(int times) {
        totalCost = totalCost + (additionalFee * times);
        otherZoneDriver = true;
    }

    // EFFECTS: returns a string about the starting point and destination of the ride.
    public String getInformation() {
        return ("from zone " + start + " to zone " + destination);
    }
}
