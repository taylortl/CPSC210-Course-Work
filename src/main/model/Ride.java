package model;

import static java.lang.Math.abs;

public class Ride {
    private static int numberBooking = 0;
    public static final int ONE_ZONE_COST = 10; // cost of a ride within one zone
    public static final int ADDITIONAL_FEE = 5; // additional cost for cross-border

    private int driver; // driver of the ride (represented in his/her number)
    private int reference; // each ride has a booking number
    private int time; // starting time
    private int start; // zone of the starting position
    private int destination; // zone of the destination
    private int cost; // cost of the ride
    private boolean additionalFee; // true if the ride calls a driver from other zones



    /*  REQUIRES: 0 <= driverNumber < the number of drivers in the list
                  1 <= startZone <  5
                  1 <= desZone <  5
        EFFECTS: initialize all the data member,
                 calculate the cost of this ride,
                 assume that the ride doesn't cost any additional fee
                 create a reference number for this ride. */
    public Ride(int driverNumber, int startZone, int desZone, int time) {
        driver = driverNumber;
        start = startZone;
        destination = desZone;
        this.time = time;
        cost = ONE_ZONE_COST + abs(start - destination) * ADDITIONAL_FEE;
        reference = numberBooking++;
        additionalFee = false;
    }

    // Getters
    public int getOneZoneCost() {
        return ONE_ZONE_COST;
    }

    public int getAdditionalCost() {
        return ADDITIONAL_FEE;
    }

    public int getReference() {
        return reference;
    }

    public int getDriver() {
        return driver;
    }

    public int getCost() {
        return cost;
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

    public boolean getAdditionalFee() {
        return additionalFee;
    }


    // MODIFIES: this
    // EFFECTS: add the additional fee if the user chooses a driver from a further zone.
    public void addFee(int times) {
        cost = cost + (ADDITIONAL_FEE * times);
        additionalFee = true;
    }

    // EFFECTS: returns a string about the starting point and destination of the ride.
    public String getInformation() {
        return ("from zone " + start + " to zone " + destination);
    }
}
