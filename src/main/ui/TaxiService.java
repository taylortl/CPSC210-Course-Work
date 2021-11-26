package ui;

import exceptions.*;
import model.Company;
import model.Customer;
import persistence.JsonReader;
import persistence.JsonWriter;
import exceptions.DriversOffWork;
import ui.exceptions.OutOfBoundInput;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
// A user interface class that prompt for values for the ride booking system.
public class TaxiService {
    private static final String JSON_FILE = "./data/kingdom.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Customer user;
    private Company kingdom;
    private Scanner input; // takes the input of the user

    // EFFECTS: initializes an empty ride list, a list of 6 drivers and run the service.
    public TaxiService() {
        // scanner for user input
        input = new Scanner(System.in);
        user = new Customer();
        kingdom = new Company(user);
        // for the json files
        jsonWriter = new JsonWriter(JSON_FILE);
        jsonReader = new JsonReader(JSON_FILE);
        runService();
        input.close();
    }

    // EFFECTS: prints a menu of the application.
    private void printMenu() {
        System.out.println("---------------------------------------");
        System.out.println("1. Rate the drivers.");
        System.out.println("2. Book a ride.");
        System.out.println("3. Cancel a ride.");
        System.out.println("4. Save my ride history");
        System.out.println("5. Load my rides");
        System.out.println("6. View price");
        System.out.println("7. Exit");
        System.out.println("---------------------------------------");
    }

    // EFFECTS: runs the application.
    private void runService() {
        boolean exit = false;
        int option;
        while (!exit) {
            printMenu();
            System.out.print("Please enter the number from the menu: ");
            option = input.nextInt();
            if (option == 7) {
                exit = true;
                System.out.println("Thank you for choosing us, we hope to see you soon!");
            } else if (option > 7 || option < 1) {
                System.out.println("Wrong option, please choose from 1 to 7.");
            } else {
                try {
                    doJobs(option);
                } catch (OutOfBoundInput e) {
                    incorrectInput();
                } catch (DriversOffWork driversOffWork) {
                    System.out.println("Sorry, our drivers off at 23:00, we cannot provide service later than that.");
                }
            }
        }
    }

    // EFFECTS: processes the option of the user.
    private void doJobs(int option) throws OutOfBoundInput, DriversOffWork {
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
                saveHistory();
                break;
            case 5:
                loadHistory();
                break;
            case 6:
                printPrice();
                break;
        }
    }

    // EFFECTS: prints the price table.
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
        List<String> rides = user.getRideHistoryUnReviewed();
        System.out.println("NOTE: RIDES THAT WERE RATED OR CANCELLED WILL NOT BE SHOWN.");
        System.out.println();
        if (!rides.isEmpty()) {
            for (String r : rides) {
                System.out.println(r);
            }
            System.out.println("---------------------------------------");
            return true;
        }
        System.out.println("---------------------------------------");
        return false;
    }

    // EFFECTS: shows a message when the user gives a wrong input.
    private void incorrectInput() {
        System.out.println("Sorry, you have typed an invalid input or wrong number. Please start again.");
    }

    //EFFECTS: prompt for reference number
    private void promptReferenceNumber(String action) throws OutOfBoundInput {
        boolean rides = printRides();
        if (!rides) {
            System.out.println("(You either did not make any booking or you have reviewed your rides)");
        } else {
            System.out.print("Please enter the reference number of the ride: ");
            int reference = input.nextInt();
            if (reference < 0 || reference >= user.numberOfRides()) {
                throw new OutOfBoundInput();
            }
            try {
                if (action.equals("cancel")) {
                    cancellation(reference);
                } else if (action.equals("rating")) {
                    doRating(reference);
                }
            } catch (ReviewedRideException e) {
                System.out.println("Please make sure you entered the reference number provided in the list.");
            } catch (RideCannotBeCancelled rideCannotBeCancelled) {
                System.out.println("Rides served by drivers from other zones cannot be cancelled.");
            }
        }
    }

    // EFFECTS: prompts the user for the reference number of the ride to rank the driver of that ride.
    private void option1() throws OutOfBoundInput {
        promptReferenceNumber("rating");
    }

    // EFFECTS: prompts the user for the rank of the driver and do the rating.
    private void doRating(int reference) throws OutOfBoundInput, ReviewedRideException {
        int driverOfRide = user.getDriverOfRide(reference);
        System.out.print("Rank from 0 - 5: ");
        double rating = input.nextDouble();
        if (rating < 0 || rating > 5) {
            throw new OutOfBoundInput();
        }
        kingdom.rateDriver(reference, rating, driverOfRide);
        System.out.println("Thank you for your advice ;)");
    }

    // EFFECTS: prompts the user for time, origin, destination of the ride for booking.
    private void option2() throws OutOfBoundInput, DriversOffWork {
        boolean correctInput = true;
        System.out.print("Please enter the time of your appointment(0-23): ");
        int time = input.nextInt();
        System.out.print("Please enter the zone of your starting point(1 - 5): ");
        int start = input.nextInt();
        System.out.print("Please enter the zone of your destination(1 - 5): ");
        int destination = input.nextInt();
        int duration = abs(start - destination) + 1;
        if (time > 23 || time < 0 || start > 5 || start < 1
                || destination > 5 || destination < 1) {
            throw new OutOfBoundInput();
        }
        booking(time, start, destination, duration);
    }

    // EFFECTS: adds a booking to the customer serving.
    private void booking(int time, int start, int destination, int duration) throws OutOfBoundInput, DriversOffWork {
        System.out.println("---------------------------------------");
        List<String> driversAvailable = kingdom.getDriversWithinZone(time, start, duration);
        if (driversAvailable.isEmpty()) {
            addFeeBooking(time, start, destination, duration);
        } else {
            int chosenDriver = choosingDriver(driversAvailable);
            receipt(time, start, destination, chosenDriver, 0);
        }
    }

    // EFFECTS: prints the given list of drivers and prompts the user to choose a driver.
    private int choosingDriver(List<String> driversAvailable) throws OutOfBoundInput {
        for (String d : driversAvailable) {
            System.out.println(d);
        }
        System.out.println("---------------------------------------");
        System.out.print("Please choose the drivers from above, enter the number of the driver: ");
        int chosenDriver = input.nextInt();
        if (chosenDriver >= kingdom.numberOfDrivers() || chosenDriver < 0) {
            throw new OutOfBoundInput();
        }
        return chosenDriver;
    }

    // MODIFIES: makes the Company to book a ride and print the receipt.
    private void receipt(int time,int start,int destination,int chosenDriver,int additional) {
        int cost = kingdom.addRide(time, start, destination, chosenDriver, additional);
        System.out.println("---------------------------------------");
        System.out.println("The cost of the ride is $ " + cost
                + ", our driver will contact you soon.");
    }

    // EFFECTS: adds booking if there's no drivers in the starting zone.
    private void addFeeBooking(int time, int start, int destination, int duration)
            throws OutOfBoundInput, DriversOffWork {
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
                int driverZone = kingdom.getDriverZone(chosenOne);
                receipt(time, start, destination, chosenOne, abs(driverZone - start));
            }
        } else if (answer == 'n') {
            System.out.println("We apologize for the inconvenience.");
        } else {
            throw new OutOfBoundInput();
        }
    }

    // EFFECTS: prompts the user for the reference number of the ride for cancellation.
    private void option3() throws OutOfBoundInput {
        promptReferenceNumber("cancel");
    }

    // EFFECTS: cancels the given ride from the user
    private void cancellation(int reference) throws RideCannotBeCancelled, ReviewedRideException {
        int cancelDriver = user.getDriverOfRide(reference);
        int start = user.getStartOfRide(reference);
        int end = user.getEndOfRide(reference);
        int time = user.getTimeOfRide(reference);
        int duration = abs(start - end) + 1;
        kingdom.cancellation(cancelDriver, time, duration, reference);
        System.out.println("Booking is cancelled, we hope to see you again.");
    }

    // EFFECTS: saves the company state to file
    private void saveHistory() {
        try {
            jsonWriter.open();
            jsonWriter.write(kingdom);
            jsonWriter.close();
            System.out.println("History saved to " + JSON_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_FILE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads company history from file
    private void loadHistory() {
        try {
            kingdom = jsonReader.read();
            System.out.println("Loaded history from " + JSON_FILE);
            user = kingdom.getUser();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_FILE);
        }
    }
}

