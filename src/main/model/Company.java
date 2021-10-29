package model;

import exceptions.ReviewedRideException;
import exceptions.RideCannotBeCancelled;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/*
    Represents the company of the taxi service system:
    The booking system is for a single day only.
 */
public class Company implements Writable {
    private List<Driver> drivers; // list of drivers working in the company
    private Customer user; // customer the company is serving
    public static final int ONE_ZONE_COST = 10; // cost of a ride within one zone
    public static final int ADDITIONAL_FEE = 5; // additional cost for crossing zones

    // EFFECTS: sets the user as the given user, initial the driver list with a list of drivers
    public Company(Customer user) {
        this.user = user;
        drivers = new ArrayList<>();
        initializeDriverList();
    }

    //Getters
    public int getOneZoneCost() {
        return  ONE_ZONE_COST;
    }

    public int getAdditionalFee() {
        return ADDITIONAL_FEE;
    }

    public int numberOfDrivers() {
        return drivers.size();
    }

    public Customer getUser() {
        return user;
    }

    // REQUIRES: 0 <= driver < number of drivers in the list
    // EFFECTS: returns the initial zone of the driver.
    public int getDriverZone(int driver) {
        return drivers.get(driver).getZone();
    }

    // MODIFIES: this
    // EFFECTS: adds drivers to the service
    //          2 drivers in zone 1,
    //          1 driver in other zones
    private void initializeDriverList() {
        drivers.add(new Driver("Steve", 1));
        drivers.add(new Driver("Jane", 2));
        drivers.add(new Driver("Jaden", 3));
        drivers.add(new Driver("Samuel", 4));
        drivers.add(new Driver("Martha", 5));
        drivers.add(new Driver("Jeremy", 1));
    }

    /*
       REQUIRES: 0 <= reference < number of rides booked, 0<= rating <=5,
                 0 <= drivers < number of drivers in the list
       MODIFIES: this
       EFFECTS: changes the rating of the given driver if possible.
     */
    public void rateDriver(int reference, double rating, int driver) throws ReviewedRideException {
        user.changeReviewStateOfRide(reference);
        drivers.get(driver).changeRating(rating);
    }

    // REQUIRES: 0 <= time <= 22, 1 <= zone <= 5
    // EFFECTS: returns  a list of string, each containing information about the drivers.
    //          It only includes drivers who are available in the given zone at that time.
    public List<String> getDriversWithinZone(int time, int zone, int duration) {
        ArrayList<String> driversAvailable = new ArrayList<>();
        boolean foundDrivers;
        String driverInfo;
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getAvailability(time) == zone) {
                foundDrivers = true;
                for (int j = time + 1; j < (time + duration + 1); j++) {
                    if (drivers.get(i).getAvailability(j) != zone) {
                        foundDrivers = false;
                    }
                }
                if (foundDrivers) {
                    driverInfo = i + ": " + drivers.get(i).getInformation();
                    driversAvailable.add(driverInfo);
                }
            }
        }
        return driversAvailable;
    }


    // REQUIRES: 0 <= time <= 22, 1 <= zone <= 5
    // EFFECTS: returns a list of string, each containing information about the drivers.
    //          It includes drivers who are available in any zone at that time.
    public List<String> getDriversOutOfZone(int time, int start, int duration) {
        ArrayList<String> driversAvailable = new ArrayList<>();
        boolean foundDrivers;
        String driverInfo;
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getAvailability(time) != 0) {
                foundDrivers = true;
                for (int j = time + 1; j < (time + duration + 1); j++) {
                    if (drivers.get(i).getAvailability(j) == 0) {
                        foundDrivers = false;
                    }
                }
                if (foundDrivers) {
                    driverInfo = i + ": " + drivers.get(i).getInformation();
                    driverInfo += " (+$" + getAddedFee(i, start) + ")";
                    driversAvailable.add(driverInfo);
                }
            }
        }
        return driversAvailable;
    }

    // REQUIRES: 0 <= driver < number of drivers created, 1 <= start <= 5
    // EFFECTS: returns the additional fee needed to book this driver.
    public int getAddedFee(int driver, int start) {
        int zone = getDriverZone(driver);
        return (ADDITIONAL_FEE * abs(zone - start));
    }

    /*
       REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5,
                 0 <= selected < number of drivers created, 0 <= additional <= 4
       MODIFIES: this
       EFFECTS: adds a ride to the list
                changes te availability of the driver,
                additional cost needed for cross-zones rides,
                returns the cost of the added ride.
    */
    public int addRide(int time, int start, int destination, int selected, int additional) {
        return addOldRide(time, start, destination, selected, additional, false);
    }

    /*
       REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5,
                 0 <= selected < number of drivers created, 0 <= additional <= 4
       MODIFIES: this
       EFFECTS: adds a ride to the list
                changes te availability of the driver,
                additional cost needed for cross-zones rides,
                returns the cost of the added ride.
    */
    public int addOldRide(int time, int start, int destination, int selected, int additional, boolean reviewed) {
        drivers.get(selected).changeAvailability(time, abs(start - destination) + 1, destination);
        String name = drivers.get(selected).getName();
        return user.addOldRide(time, start, destination, selected, additional, name,
                ONE_ZONE_COST, ADDITIONAL_FEE, reviewed);
    }

    /*
       REQUIRES: 0 <= drivers < number of drivers in the list, 0 <= time <= 22,
                 0 <= rideNumber < number of rides booked,
       MODIFIES: this
       EFFECTS: sets the driver to be available in its original zone during that period.
                removes the ride from the customer.
                throws exceptions if the ride cannot be cancelled
     */
    public void cancellation(int driver, int time, int duration, int reference)
            throws ReviewedRideException, RideCannotBeCancelled {
        user.cancel(reference);
        drivers.get(driver).availableAgain(time, duration);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("customer", user.toJson());
        return json;
    }
}
