package model;

import org.json.JSONObject;
import persistence.Writable;

import static java.lang.Math.abs;

/*
    Represent a ride booked by a customer.
    Cost of ride depends on zone numbers and each ride is assigned to a reference number.
 */
public class Ride implements Writable {
    private int driver; // driver of the ride (represented in his/her number)
    private String driverName;
    private int time; // starting time
    private int start; // zone of the origin
    private int destination; // zone of the destination
    private int totalCost; // cost of the ride
    private int withinZoneFee; // cost of within-zone rides
    private int additionalFee; // additional cost for each zone crossed
    private int otherZoneDriver; // additional cost for calling drivers from other zones
    private double reviewed;



    // REQUIRES: 0 <= driverNumber < the number of drivers in the list, 1 <= startZone <  5, 1 <= desZone <  5
    //           0 <= time <= 22, oneZone > 0, multiZone > 0
    // EFFECTS: initializes all the data member
    //          calculate the cost of this ride,
    //          assumes that the ride doesn't cost any additional fee
    //          create a reference number for this ride.
    public Ride(int driverNumber, String driverName, int startZone, int desZone, int time,
                int oneZone, int multiZone, double reviewed) {
        setUp(driverNumber, driverName, startZone, desZone, time, oneZone, multiZone, reviewed);
    }

    public Ride(int driverNumber, String driverName, int startZone, int desZone, int time,
                int oneZone, int multiZone) {
        setUp(driverNumber, driverName, startZone, desZone, time, oneZone, multiZone, -1);
    }

    private void setUp(int driverNumber, String driverName, int startZone, int desZone, int time,
                       int oneZone, int multiZone, double reviewed) {
        driver = driverNumber;
        this.driverName = driverName;
        start = startZone;
        destination = desZone;
        this.time = time;
        withinZoneFee = oneZone;
        additionalFee = multiZone;
        totalCost = withinZoneFee + abs(start - destination) * additionalFee;
        otherZoneDriver = 0;
        this.reviewed = reviewed;
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

    public int getOtherZoneDriver() {
        return otherZoneDriver;
    }

    public boolean isReviewed() {
        return (reviewed != -1);
    }

    public void setReviewed(double rating) {
        this.reviewed = rating;
    }

    // REQUIRES: times > 0
    // MODIFIES: this
    // EFFECTS: adds the additional fee if the user chooses a driver from other zones.
    public void addFee(int times) {
        totalCost = totalCost + (additionalFee * times);
        otherZoneDriver = times;
    }

    // EFFECTS: returns a string about the information of the ride.
    public String getInformation() {

        return ("from zone " + start + " to zone " + destination + " at " + time + ":00");
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("driver", driver);
        json.put("startTime", time);
        json.put("startZone", start);
        json.put("destination", destination);
        json.put("additionalFee", otherZoneDriver);
        json.put("reviewed", reviewed);
        return json;
    }

}
