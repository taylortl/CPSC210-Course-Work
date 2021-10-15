package model;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/*
    Company of the taxi service system, contains a list of drivers working for the company,
    the customer it is currently serving. Most of the operations are done here.
 */
public class Company {
    private List<Driver> drivers; // list of drivers working in the company
    private Customer user; // customer the company is serving right now
    public static final int ONE_ZONE_COST = 10; // cost of a ride within one zone
    public static final int ADDITIONAL_FEE = 5; // additional cost for cross-border

    // EFFECTS: set the user as the given user, initial the driver list with a list of drivers
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

    // REQUIRES: 0 <= driver < number of drivers in the list
    // EFFECTS: return the initial zone of the driver.
    public int getDriverZone(int driver) {
        return drivers.get(driver).getZone();
    }

    // MODIFIES: this
    // EFFECTS: add drivers to the service
    //          there must be 2 drivers in zone 1, and 1 driver in other zones.
    private void initializeDriverList() {
        drivers.add(new Driver("Steve", 1));
        drivers.add(new Driver("Jane", 2));
        drivers.add(new Driver("Jaden", 3));
        drivers.add(new Driver("Samuel", 4));
        drivers.add(new Driver("Martha", 5));
        drivers.add(new Driver("Jeremy", 1));
    }

    /* REQUIRES: 0 <= reference < number of rides booked, 0<= ranking <=5,
                 0 <= drivers < number of drivers in the list
       MODIFIES: this
       EFFECTS: changes the ranking of the driver according to the given ranking from user.
                Returns true if successfully review the ride.
     */
    public boolean writeReview(int reference, double ranking, int driver) {
        boolean success = user.changeReviewStateOfRide(reference);
        if (success) {
            drivers.get(driver).changeRanking(ranking);
            return true;
        }
        return false;
    }

    /*
       REQUIRES: 0 <= time <= 23, 1 <= zone <= 5, 1<= duration <=4
       EFFECTS: returns  a list of string, each containing information about the drivers.
                It only includes drivers who are in the given zone at that time.
    */
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

    /*
       REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1<= duration <=4
       EFFECTS: returns a list of string, each containing information about the drivers.
                It includes drivers who are available at that time.
    */
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

    /*
        REQUIRES: 0 <= driver < number of drivers created, 1 <= start <= 5
        EFFECTS: returns the additional fee needed to book this driver.
     */
    public int getAddedFee(int driver, int start) {
        int zone = getDriverZone(driver);
        return (ADDITIONAL_FEE * abs(zone - start));
    }

    /*
            REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5,
                      0 <= selected < number of drivers created, 0 <= additional <= 4
            MODIFIES: this
            EFFECTS: add a ride to the list and change te availability of the driver,
                     additional cost needed if origin and destination are not in the same zone,
                     returns the cost of the added ride.
         */
    public int addRide(int time, int start, int destination, int selected, int additional) {
        drivers.get(selected).changeAvailability(time, abs(start - destination) + 1, destination);
        String name = drivers.get(selected).getName();
        return user.addRide(time, start, destination, selected, additional, name, ONE_ZONE_COST, ADDITIONAL_FEE);
    }

    /*
        REQUIRES: 0 <= drivers < number of drivers in the list, 0 <= time <= 23,
                  1<= duration <=4, 0 <= rideNumber < number of rides booked,
        MODIFIES: this
        EFFECTS: set the driver to be available in its original zone in that period.
                 call the cancel function in Customer class to remove the ride.
     */
    public void cancellation(int driver, int time, int duration, int rideNumber) {
        user.cancel(rideNumber);
        drivers.get(driver).availableAgain(time, duration);
    }
}
