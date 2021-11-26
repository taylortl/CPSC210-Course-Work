package model;

import exceptions.ReviewedRideException;
import exceptions.RideCannotBeCancelled;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
// Represents a customer booking rides from the company
public class Customer implements Writable {
    private List<Ride> rides; // ride history of the customer

    // EFFECTS: initialize the booking list to empty.
    public Customer() {
        rides = new ArrayList<>();
    }

    // EFFECTS: returns the number of bookings.
    public int numberOfRides() {
        return (rides.size());
    }

    // REQUIRES: 0 <= reference < number of rides booked
    // EFFECTS: returns the driver of the given ride.
    public int getDriverOfRide(int reference) {
        return rides.get(reference).getDriver();
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: returns the starting zone of the given ride.
    public int getStartOfRide(int reference) {
        return rides.get(reference).getStart();
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: returns the destination of the given ride.
    public int getEndOfRide(int reference) {
        return rides.get(reference).getDestination();
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: returns the time of the given ride.
    public int getTimeOfRide(int reference) {
        return rides.get(reference).getTime();
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // MODIFIES: this
    // EFFECTS: rates the driver if the ride is not reviewed
    //          throws an exception if the ride has been reviewed
    public String changeReviewStateOfRide(int reference, double rating) throws ReviewedRideException {
        if (rides.get(reference).isReviewed()) {
            throw new ReviewedRideException();
        }
        rides.get(reference).setReviewed(rating);
        return "Ride rated: " + rides.get(reference).getInformation();
    }

    // EFFECTS: returns a list of unreviewed rides the user booked.
    public List<String> getRideHistoryUnReviewed() {
        ArrayList<String> outputInformation = new ArrayList<>();
        String information;
        for (int i = 0; i < rides.size(); i++) {
            if (!rides.get(i).isReviewed()) {
                information = i + ": ";
                information += rides.get(i).getDriverName() + " driving you ";
                information += rides.get(i).getInformation();
                outputInformation.add(information);
            }
        }
        return outputInformation;
    }

    // EFFECTS: returns a list of rides the user booked.
    public List<String> getRideHistory() {
        ArrayList<String> outputInformation = new ArrayList<>();
        String information;
        for (int i = 0; i < rides.size(); i++) {
                information = i + ": ";
                information += rides.get(i).getDriverName() + " driving you ";
                information += rides.get(i).getInformation();
                outputInformation.add(information);
        }
        return outputInformation;
    }

    /*
        REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5,
                  0 <= selected < number of drivers created, 0 <= additional <= 4
        MODIFIES: this
        EFFECTS: adds a ride to the list
                 adds the additional fee if given.
                 returns the cost of the ride.
     */
    public int addRide(int time, int start, int end, int selected, int additional,
                       String name, int cost, int fee) {
        return addOldRide(time, start, end, selected, additional, name, cost, fee, -1);
    }

    public int addOldRide(int time, int start, int end, int selected, int additional,
                       String name, int cost, int fee, double reviewed) {
        Ride newRide = new Ride(selected, name, start, end, time, cost, fee, reviewed);
        if (additional > 0) {
            newRide.addFee(additional);
        }
        int totalCost = newRide.getTotalCost();
        rides.add(newRide);
        return totalCost;
    }


    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: removes the given ride from the list if it's an accessible ride.
    //          throws an exception otherwise
    public String cancel(int reference) throws ReviewedRideException, RideCannotBeCancelled {
        if (rides.get(reference).isReviewed()) {
            throw new ReviewedRideException();
        } else if (rides.get(reference).getOtherZoneDriver() > 0) {
            throw new RideCannotBeCancelled();
        }
        String description = "Ride cancelled: " + rides.get(reference).getInformation();
        rides.remove(reference);
        return description;
    }

    // EFFECTS: returns a Json object of the Customer object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("rides", ridesToJson());
        return json;
    }

    // EFFECTS: returns rides of this customer as a JSON array
    private JSONArray ridesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Ride r : rides) {
            jsonArray.put(r.toJson());
        }
        return jsonArray;
    }
}
