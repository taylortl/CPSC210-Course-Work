package model;

import java.util.ArrayList;
import java.util.List;

// A customer contains a list of booking.
public class Customer {
    private List<Ride> rides; // list of booking

    // EFFECTS: initialize the booking list to empty.
    public Customer() {
        rides = new ArrayList<>();
    }

    // EFFECTS: returns that number of bookings.
    public int numberOfRides() {
        return (rides.size());
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: return the driver of the corresponding ride.
    public int getDriverOfRide(int rideReference) {
        return rides.get(rideReference).getDriver();
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: return the starting zone of the corresponding ride.
    public int getStartOfRide(int rideReference) {
        return rides.get(rideReference).getStart();
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: return the destination of the corresponding ride.
    public int getEndOfRide(int rideReference) {
        return rides.get(rideReference).getDestination();
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: return the time of the corresponding ride.
    public int getTimeOfRide(int rideReference) {
        return rides.get(rideReference).getTime();
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // MODIFIES: this
    // EFFECTS: return true if successfully review the ride.
    public boolean changeReviewStateOfRide(int rideReference) {
        if (rides.isEmpty() || rides.get(rideReference).isReviewed()) {
            return false;
        }
        rides.get(rideReference).setReviewed();
        return true;
    }

    // EFFECTS: prints a list of rides the user booked.
    public List<String> getRideHistory() {
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

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5,
                  0 <= selected < number of drivers created, 0 <= additional <= 4
        MODIFIES: this
        EFFECTS: add a ride to the list, add the additional fee if driver is not at the starting zone.
     */
    public int addRide(int time, int start, int end, int selected, int additional, String name, int cost, int fee) {
        Ride newRide = new Ride(selected, name, start, end, time, cost, fee);
        if (additional > 0) {
            newRide.addFee(additional);
        }
        int totalCost = newRide.getTotalCost();
        rides.add(newRide);
        return totalCost;
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: returns the driver number of the ride if it can be cancelled.
    public int cancellable(int rideNumber) {
        if (rides.isEmpty() || rides.get(rideNumber).getOtherZoneDriver()) {
            return -1;
        }
        return rides.get(rideNumber).getDriver();
    }

    // REQUIRES: 0 <= rideReference < number of rides in the list
    // EFFECTS: remove the given ride from the list.
    public void cancel(int rideNumber) {
        rides.remove(rideNumber);
    }
}
