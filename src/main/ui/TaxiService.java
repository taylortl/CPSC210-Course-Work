package ui;

import model.Company;
import model.Customer;

import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;

// A user interface class that prompt for values for the ride booking system.
public class TaxiService {
    private Customer user;
    private Company kingdom;
    private Scanner input; // takes the input of the user

    // EFFECTS: initialize an empty ride list, a list of 6 drivers and run the service.
    public TaxiService() {
        input = new Scanner(System.in);
        user = new Customer();
        kingdom = new Company(user);
        runService();
        input.close();
    }

    // EFFECTS: prints a menu of the application.
    private void printMenu() {
        System.out.println("---------------------------------------");
        System.out.println("1. Rate the drivers.");
        System.out.println("2. Book a ride.");
        System.out.println("3. Cancel a ride.");
        System.out.println("4. View price");
        System.out.println("5. Exit");
        System.out.println("---------------------------------------");
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

    // EFFECTS: print the price table.
    private void printPrice() {
        System.out.println("NOTE: RIDES SERVED BY DRIVERS OUTSIDE THE STARTING ZONE CANNOT BE CANCELLED.");
        System.out.println();
        System.out.println("Ride within 1 zone   - $" + kingdom.getOneZoneCost());
        System.out.println("Additional cost for ride between zones   - +$" + kingdom.getAdditionalFee() + " /zone");
        System.out.println("Additional cost for booking driver outside the starting zone   - +$"
                + kingdom.getAdditionalFee() + " /zone");


    }

    // EFFECTS: prints a list of rides the user booked, rides that are cancelled or reviewed will not be shown.
    private boolean printRides() {
        List<String> rides = user.getRideHistory();
        System.out.println("NOTE: RIDES THAT WERE RATED OR CANCELLED WILL NOT BE SHOWN.");
        System.out.println();
        if (rides.isEmpty()) {
            return false;
        }
        for (String r : rides) {
            System.out.println(r);
        }
        System.out.println("---------------------------------------");
        return true;
    }

    // EFFECTS: show a message when the user gives a wrong input.
    private void incorrectInput() {
        System.out.println("Sorry, you have typed an invalid input or wrong number. Please start again.");
    }

    // EFFECTS: prompt the user for the reference number of the ride to rank the driver of that ride.
    private void option1() {
        if (user.numberOfRides() == 0) {
            System.out.println("Sorry, you did not make any booking.");
        } else {
            boolean rides = printRides();
            if (rides) {
                System.out.print("Please enter the reference number of the ride: ");
                int reference = input.nextInt();
                if (reference >= 0 && reference < user.numberOfRides()) {
                    doRating(reference);
                } else {
                    incorrectInput();
                }
            }
        }
    }

    // REQUIRES: 0 <= reference < number of rides booked
    // EFFECTS: prompt the user for the rank of the driver and do the rating.
    private void doRating(int reference) {
        int driverOfRide = user.getDriverOfRide(reference);
        System.out.print("Rank from 0 - 5: ");
        double rating = input.nextDouble();
        boolean success = kingdom.rateDriver(reference, rating, driverOfRide);
        if (success) {
            System.out.println("Thank you for your advice.");
        } else {
            incorrectInput();
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
            int duration = abs(start - destination) + 1;
            booking(time, start, destination, duration);
        } else {
            incorrectInput();
        }
    }

    // REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5, 1<= duration <=4
    // EFFECTS: add a booking.
    private void booking(int time, int start, int destination, int duration) {
        System.out.println("---------------------------------------");
        List<String> driversAvailable = kingdom.getDriversWithinZone(time, start, duration);
        if (driversAvailable.isEmpty()) {
            addFeeBooking(time, start, destination, duration);
        } else {
            int chosenDriver = choosingDriver(driversAvailable);
            if (chosenDriver >= 0) {
                receipt(time, start, destination, chosenDriver, 0);
            }
        }
    }

    // EFFECTS: print the given list of drivers and prompt the user to choose a driver.
    private int choosingDriver(List<String> driversAvailable) {
        for (String d : driversAvailable) {
            System.out.println(d);
        }
        System.out.println("---------------------------------------");
        System.out.print("Please choose the drivers from above, enter the number of the driver: ");
        int chosenDriver = input.nextInt();
        if (chosenDriver >= kingdom.numberOfDrivers() || chosenDriver < 0) {
            incorrectInput();
            chosenDriver = -1;
        }
        return chosenDriver;
    }

    // REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5,
    //           0 <= drivers < number of drivers in the list, 1 <= additional <= 4
    // MODIFIES: make the Company to book a ride and print the receipt.
    private void receipt(int time,int start,int destination,int chosenDriver,int additional) {
        int cost = kingdom.addRide(time, start, destination, chosenDriver, additional);
        System.out.println("---------------------------------------");
        System.out.println("The cost of the ride is $ " + cost
                + ", our driver will contact you soon.");
    }

    //REQUIRES: 0 <= time <= 23, 1 <= start <= 5, 1 <= destination <= 5, 1 <= duration <= 4
    // EFFECTS: booking if there's no drivers in the starting zone.
    private void addFeeBooking(int time, int start, int destination, int duration) {
        System.out.println("There's no driver available in the zone at that time. ");
        System.out.print("Would you like to choose drivers from other zones? (y/n)? ");
        input.nextLine();
        char answer = input.nextLine().charAt(0);
        if (answer == 'y') {
            List<String> driversAvailable = kingdom.getDriversOutOfZone(time, start, duration);
            if (driversAvailable.isEmpty()) {
                System.out.print("Sorry, there's no other drivers available at that time. We hope to see you again.");
            } else {
                int chosenOne = choosingDriver(driversAvailable);
                if (chosenOne >= 0 && chosenOne < kingdom.numberOfDrivers()) {
                    int driverZone = kingdom.getDriverZone(chosenOne);
                    receipt(time, start, destination, chosenOne, abs(driverZone - start));
                }
            }
        } else if (answer == 'n') {
            System.out.println("We apologize for the inconvenience.");
        }
    }

    // EFFECTS: prompt the user for the reference number of the ride for cancellation.
    private void option3() {
        if (user.numberOfRides() == 0) {
            System.out.println("Sorry, you did not make any booking.");
        } else {
            boolean rides = printRides();
            if (rides) {
                System.out.print("Please enter the reference number of the ride: ");
                int reference = input.nextInt();
                if (reference < user.numberOfRides() && reference >= 0) {
                    cancellation(reference);
                } else {
                   incorrectInput();
                }
            } else {
                System.out.println("Reviewed rides cannot be cancelled.");
            }
        }
    }

    // REQUIRES: 0 <= rideNumber < number of rides booked
    // EFFECTS: cancel the chosen ride of the user
    private void cancellation(int reference) {
        int cancelDriver = user.cancellable(reference);
        if (cancelDriver < 0) {
            System.out.println("Sorry, this booking cannot be cancelled.");
        } else {
            int start = user.getStartOfRide(reference);
            int end = user.getEndOfRide(reference);
            int time = user.getTimeOfRide(reference);
            int duration = abs(start - end) + 1;
            kingdom.cancellation(cancelDriver, time, duration, reference);
            System.out.println("Booking is cancelled, we hope to see you again.");
        }
    }
}
