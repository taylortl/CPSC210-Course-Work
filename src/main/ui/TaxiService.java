package ui;

import model.Driver;
import model.Ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;

public class TaxiService {
    private static final int ONE_ZONE_COST = 10; // cost of a ride within one zone
    private static final int ADDITIONAL_FEE = 5; // additional cost for cross-border

    private List<Ride> rides; // list of rides of the user
    private Scanner input; // takes the input of the user
    private List<Driver> drivers; // list of drivers serving the user

    // EFFECTS: initialize an empty ride list, a list of 6 drivers and run the service.
    public TaxiService() {
        input = new Scanner(System.in);
        rides = new ArrayList<>();
        drivers = new ArrayList<>();
        initializeDriverList();
        runService();
        input.close();
    }

    // MODIFIES: this
    // EFFECTS: add drivers to the service.
    private void initializeDriverList() {
        drivers.add(new Driver("Steve", 1));
        drivers.add(new Driver("Jane", 2));
        drivers.add(new Driver("Jaden", 3));
        drivers.add(new Driver("Samuel", 4));
        drivers.add(new Driver("Martha", 5));
        drivers.add(new Driver("Jeremy", 1));
    }

    // EFFECTS: run the application.
    private void runService() {
        boolean exit = false;
        int option;
        while (!exit) {
            printMenu();
            System.out.print("Please enter the number from the menu: ");
            option = input.nextInt();
            if (option == 5) {
                exit = true;
                System.out.println("Thank you for choosing us, we hope to see you soon!");
            } else if (option > 5 || option < 1) {
                System.out.println("Wrong option, please choose from 1 to 4.");
            } else {
                doJobs(option);
            }
        }
    }

    // EFFECTS: print the price table.
    private void printPrice() {
        System.out.println("---------------------------------------");
        System.out.println("Ride within 1 zone   $" + ONE_ZONE_COST);
        System.out.println("Additional cost for ride between zones   +$" + ADDITIONAL_FEE + " /zone");
        System.out.println("Additional cost for booking driver from other zones   +$" + ADDITIONAL_FEE + " /zone");
        System.out.println("NOTE: Rides that booked driver from other zones cannot be cancelled");
        System.out.println("---------------------------------------");

    }

    // EFFECTS: prints a menu of the application.
    private void printMenu() {
        System.out.println("---------------------------------------");
        System.out.println("1. Write a review for the drivers.");
        System.out.println("2. Book a ride.");
        System.out.println("3. Cancel a ride.");
        System.out.println("4. View price");
        System.out.println("5. Exit");
        System.out.println("---------------------------------------");
    }

    // REQUIRES: 0 <= driver < number of drivers created
    // EFFECTS: returns the name of the driver given the id of the driver.
    private String getDriverName(int driver) {
        for (Driver d : drivers) {
            if (d.getId() == driver) {
                return d.getName();
            }
        }
        return "";
    }

    // EFFECTS: prints a list of rides the user booked.
    private void printRides() {
        System.out.println("---------------------------------------");
        for (Ride r : rides) {
            System.out.print(r.getReference() + ": ");
            System.out.print(getDriverName(r.getDriver()) + " driving you ");
            System.out.println(r.getInformation());
        }
        System.out.println("---------------------------------------");
    }

    // EFFECTS: show a message when the user gives a wrong input.
    private void incorrectInput() {
        System.out.println("Sorry, you have typed an invalid input. Please start again.");
    }

    // EFFECTS: prompt the user for the reference number of the ride to rank the driver of that ride.
    private void option1() {
        if (rides.isEmpty()) {
            System.out.println("Sorry, you did not make any booking.");
        } else {
            printRides();
            System.out.print("Please enter the reference number of the ride: ");
            int reference = input.nextInt();
            if (reference >= rides.size() || reference < 0) {
                incorrectInput();
            } else {
                writeReview(reference);
            }
        }
    }

    // EFFECTS: prompt the user for time, origin, destination of the ride for booking.
    private void option2() {
        boolean correctInput = true;
        System.out.print("Please enter the time of your appointment(0-23): ");
        int time = input.nextInt();
        if (time > 23 || time < 0) {
            correctInput = false;
        }
        System.out.print("Please enter the zone of your starting point(1 - 5): ");
        int start = input.nextInt();
        if (start > 5 || start < 1) {
            correctInput = false;
        }
        System.out.print("Please enter the zone of your destination(1 - 5): ");
        int destination = input.nextInt();
        if (destination > 5 || destination < 1) {
            correctInput = false;
        }
        if (correctInput) {
            booking(time, start, destination);
        } else {
            incorrectInput();
        }
    }

    // EFFECTS: prompt the user for the reference number of the ride for cancellation.
    private void option3() {
        if (rides.isEmpty()) {
            System.out.println("Sorry, you did not make any booking.");
        } else {
            printRides();
            System.out.print("Please enter the reference number of the ride: ");
            int reference = input.nextInt();
            if (reference >= rides.size() || reference < 0) {
                incorrectInput();
            } else {
                cancellation(reference);
            }
        }
    }

    // EFFECTS: process the option of the user.
    private void doJobs(int option) {
        System.out.println("---------------------------------------");
        switch (option) {
            case 1:
                option1();
                break;
            case 2:
                option2();
                break;
            case 3:
                option3();
                break;
            case 4:
                printPrice();
                break;
        }
    }

    /* REQUIRES: 0 <= driver < number of drivers created, 0 <= ranking <= 5
       MODIFIES: this
       EFFECTS: update the ranking of the given driver.
     */
    private void changeRanking(int driver, double ranking) {
        for (Driver d : drivers) {
            if (d.getId() == driver) {
                d.changeRanking(ranking);
                break;
            }
        }
        System.out.println("Thank you for giving us advice.");
    }

    /* REQUIRES: 0 <= reference < number of rides booked
       MODIFIES: this
       EFFECTS: change the ranking of the driver according to the given ranking from user.
     */
    private void writeReview(int reference) {
        boolean rideFound = false;
        int driver = -1;
        double ranking = 0;
        for (Ride r : rides) {
            if (reference == r.getReference()) {
                rideFound = true;
                System.out.println("Rank from 0 - 5 ");
                ranking = input.nextDouble();
                if (ranking < 0 || ranking > 5) {
                    incorrectInput();
                } else {
                    driver = r.getDriver();
                }
                break;
            }
        }
        if (!rideFound) {
            System.out.println("Sorry you input the wrong booking number.");
        } else {
            changeRanking(driver, ranking);
        }
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5
        EFFECTS: print a list of drivers available at that time in that zone,
                 returns true if there's driver available,
                 returns false if no drivers available.
     */
    private boolean printDriversInZone(int time, int start, int duration) {
        boolean foundDrivers = false;
        int driverZone;
        for (Driver d : drivers) {
            driverZone = d.getAvailability(time);
            if (driverZone == start) {
                foundDrivers = true;
                for (int i = time + 1; i < (time + duration + 1); i++) {
                    if (d.getAvailability(i) != start) {
                        foundDrivers = false;
                    }
                }
                if (foundDrivers) {
                    System.out.println(d.getInformation());
                }
            }
        }
        return foundDrivers;
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5
        EFFECTS: print a list of drivers available at that time in any zones,
                 returns true if there's driver available,
                 returns false if no drivers available.
     */
    private boolean printDrivers(int time, int start, int duration) {
        boolean foundDrivers = false;
        int driverZone;
        for (Driver d : drivers) {
            driverZone = d.getAvailability(time);
            if (driverZone != 0) {
                foundDrivers = true;
                for (int i = time + 1; i < (time + duration + 1); i++) {
                    if (d.getAvailability(i) == 0) {
                        foundDrivers = false;
                    }
                }
                if (foundDrivers) {
                    System.out.print(d.getInformation());
                    System.out.println(" (+$" + abs(start - driverZone) * ADDITIONAL_FEE + ")");
                }
            }
        }
        return foundDrivers;
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5,
                  0 <= selected < number of drivers created, 0 <= additional <= 4
        MODIFIES: this
        EFFECTS: add a ride to the list and change te availability of the driver,
                 additional cost needed if origin and destination are not in the same zone.
     */
    private void addRide(int time, int start, int destination, int selected, int additional) {
        for (Driver d : drivers) {
            if (d.getId() == selected) {
                d.changeAvailability(time, abs(start - destination) + 1, destination);
                break;
            }
        }
        Ride newRide = new Ride(selected, start, destination, time);
        newRide.addFee(additional);
        rides.add(newRide);
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5,
                  0 <= selected < number of drivers created, 0 <= additional <= 4
        EFFECTS: print the cost of the ride after adding the ride to the list.
     */
    private void receipt(int time, int start, int destination, int selected, int additional) {
        addRide(time, start, destination, selected, additional);
        System.out.println("---------------------------------------");
        System.out.println("The cost of the ride is $ " + rides.get(rides.size() - 1).getCost()
                + " ,our driver will contact you soon.");
    }

    // REQUIRES: 0 <= selected < number of drivers created, 0 <= time <= 23
    // EFFEECTS: returns the zone of the given driver.
    private int getDriverZone(int selected, int time) {
        int availability = 0;
        for (Driver d : drivers) {
            if (d.getId() == selected) {
                availability = d.getAvailability(time);
                break;
            }
        }
        return availability;
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5, answer = y or answer = n
        EFFECTS: book a driver from other zone if the user agree
     */
    private void bookOtherZonesDriver(int time, int start, int destination, int answer, int duration) {
        if (answer == 'y') {
            boolean matched = printDrivers(time, start, duration);
            System.out.println("---------------------------------------");
            if (matched) {
                System.out.print("Please choose the drivers from above, enter the number of the driver: ");
                int selected = input.nextInt();
                if (selected >= drivers.size() || selected < 0) {
                    incorrectInput();
                } else {
                    int driverZone = getDriverZone(selected, time);
                    receipt(time, start, destination, selected, abs(start - driverZone));
                }
            } else {
                System.out.print("Sorry, there's no other drivers available at that time. We hope to see you again.");
            }
        } else {
            System.out.println("We apologize for the inconvenience.");
        }
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5
        EFFECTS: prompt the user to decide if they agree to book a driver from other zones.
     */
    private void assignOtherDriver(int time, int start, int destination, int duration) {
        System.out.println("There's no driver available in the zone at that time. ");
        System.out.print("Would you like to choose drivers from other zones? (y/n)? ");
        input.nextLine();
        char answer = input.nextLine().charAt(0);
        if (answer == 'y' || answer == 'n') {
            bookOtherZonesDriver(time, start, destination, answer, duration);
        } else {
            incorrectInput();
        }
    }

    /*
        REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5
        EFFECTS: make booking with the selected driver (if drivers available),
                 assign drivers from other zone if none of them are available.
     */
    private void booking(int time, int start, int destination) {
        System.out.println("---------------------------------------");
        int duration = abs(start - destination) + 1;
        boolean matched = printDriversInZone(time, start, duration);
        if (matched) {
            System.out.println("---------------------------------------");
            System.out.print("Please choose the drivers from above, enter the number of the driver: ");
            int selected = input.nextInt();
            if (selected >= drivers.size() || selected < 0) {
                incorrectInput();
            } else {
                receipt(time, start, destination, selected, 0);
            }
        } else {
            assignOtherDriver(time, start, destination, duration);
        }
    }

    /* REQUIRES: 0 <= driver < number of drivers created, 0 <= ride < number of rides booked, 1 <= duration <= 4
       MODIFIES: this
       EFFECTS: make the driver available for booking during the time of the given ride.
     */
    private void cancelRideForDriver(int driver, int ride, int duration) {
        int time = -1;
        for (Ride r : rides) {
            if (r.getReference() == ride) {
                time = r.getTime();
                break;
            }
        }
        for (Driver d : drivers) {
            if (d.getId() == driver) {
                d.availableAgain(time, duration);
                break;
            }
        }
    }

    /*
        REQUIRES: 0 <= rideNumber < number of rides booked
        MODIFIES: this
        EFFECTS: remove the ride from our list,
                 set the driver to be available in its original zone in that period.
     */
    private void cancellation(int rideNumber) {
        int remove = -1;
        int driver;
        int duration;
        boolean cancel = false;
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getReference() == rideNumber) {
                if (rides.get(i).getAdditionalFee()) {
                    System.out.println("Sorry, this booking cannot be cancelled.");
                } else {
                    cancel = true;
                    remove = i;
                    driver = rides.get(i).getDriver();
                    duration = abs(rides.get(i).getStart() - rides.get(i).getDestination());
                    cancelRideForDriver(driver, i, duration);
                }
                break;
            }
        }
        if (cancel) {
            rides.remove(remove);
            System.out.println("Booking is cancelled, we hope to see you again.");
        }
    }

}
