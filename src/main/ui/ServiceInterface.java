package ui;

import exceptions.DriversOffWork;
import exceptions.ReviewedRideException;
import exceptions.RideCannotBeCancelled;
import model.Company;
import model.Customer;
import model.EventLog;
import persistence.JsonReader;
import persistence.JsonWriter;
import ui.exceptions.OutOfBoundInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.abs;

// User interface for the ride booking system
public class ServiceInterface extends JFrame {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 400;

    private static final String JSON_FILE = "./data/history.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Customer user;
    private Company kingdom;

    private JPanel outputPanel;
    private JPanel buttonPanel;
    private GridBagConstraints format;

    private JTextField timeField;
    private JTextField startField;
    private JTextField endField;
    private JTextField driverField;
    private JTextField referenceField;
    private JTextField ratingField;
    private JRadioButton yes;

    // EFFECTS: create an ui object for the ride booking system with initialize graphics and data
    public ServiceInterface() {
        super("Ride Booking System");
        initialization();
        initializeGraphics();
        loadHistory();
    }

    // MODIFIES: this
    // EFFECTS:  add buttons to the interface frame
    private void addButton() {
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(WIDTH / 4, HEIGHT));
        buttonPanel.setLayout(new GridBagLayout());
        JButton printPrice = new JButton(new PrintPrice());
        JButton rateDrivers = new JButton(new RateDrivers());
        JButton cancelRide = new JButton(new Cancel());
        JButton bookRide = new JButton(new Booking());
        buttonPanel.add(printPrice, format);
        buttonPanel.add(rateDrivers, format);
        buttonPanel.add(cancelRide, format);
        buttonPanel.add(bookRide, format);
    }

    // MODIFIES: this
    // EFFECTS: initialize the frame design
    private void initializeGraphics() {
        // set the size of the application
        setSize(WIDTH, HEIGHT);
        // set that the application cannot be resized
        setResizable(false);
        // set the layout of the frame
        setLayout(new BorderLayout());
        // set the program will exit when the exit button is clicked
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        add(outputPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.WEST);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveRides();
                Iterator<model.Event> log = EventLog.getInstance().iterator();
                while (log.hasNext()) {
                    System.out.println(log.next().toString() + "\n\n");
                }
            }
        });
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: create the customer serving and the company
    private void initialization() {
        user = new Customer();
        kingdom = new Company(user);
        jsonWriter = new JsonWriter(JSON_FILE);
        jsonReader = new JsonReader(JSON_FILE);
        format = new GridBagConstraints();
        format.gridwidth = GridBagConstraints.REMAINDER;
        format.fill = GridBagConstraints.HORIZONTAL;
        outputPanel = new JPanel();
        outputPanel.setPreferredSize(new Dimension(WIDTH * 3 / 4, HEIGHT));
        outputPanel.setLayout(new GridBagLayout());
        addButton();
    }

    // MODIFIES: this
    // EFFECTS: load ride history from json file
    private void loadHistory() {
        int response = JOptionPane.showConfirmDialog(null,
                "Load previous rides?", "Load History",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            loadFromJson();
        }
    }

    // EFFECTS: save the rides made by the customer today
    private void saveRides() {
        int response = JOptionPane.showConfirmDialog(null,
                "Do you want to save your rides?", "Save",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            saveToJson();
        }
        dispose();
    }

    // EFFECTS: saves the rides made to file
    private void saveToJson() {
        try {
            jsonWriter.open();
            jsonWriter.write(kingdom);
            jsonWriter.close();
            JOptionPane.showMessageDialog(null, "History saved to " + JSON_FILE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + JSON_FILE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads company history from file
    private void loadFromJson() {
        try {
            kingdom = jsonReader.read();
            JOptionPane.showMessageDialog(null, "Loaded history from " + JSON_FILE);
            user = kingdom.getUser();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to read from file: " + JSON_FILE);
        }
    }

    // MODIFIES: this
    // EFFECTS: remove all the stuff on the right side of the frame
    private void removeComponents() {
        Component[] removable = outputPanel.getComponents();
        for (Component r : removable) {
            outputPanel.remove(r);
        }
    }

    // MODIFIES: this
    // EFFECTS: update the right side of the frame
    private void updatePanel() {
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    /* ----------------------------Actions---------------------------- */

    // REQUIRES: action == "cancel" || action == "rating"
    // EFFECTS: prompt the user for reference number after showing rides to the user
    private void promptReferenceNumber(String action) {
        List<String> rides = user.getRideHistory();
        JLabel notice = new JLabel("NOTE: RIDES THAT WERE RATED OR CANCELLED WILL NOT BE SHOWN.");
        outputPanel.add(notice, format);
        if (rides.isEmpty()) {
            JLabel noRides = new JLabel("(no booking found or rides are reviewed)");
            outputPanel.add(noRides, format);
        } else {
            outputPanel.add(new JList(rides.toArray()), format);
            JLabel promptInput = new JLabel("Please enter the reference number of the ride: ");
            outputPanel.add(promptInput, format);
            referenceField = new JTextField(10);
            outputPanel.add(referenceField, format);
            JButton submit = new JButton("Process");
            submit.addActionListener(new PromptReferenceListener(action));
            outputPanel.add(submit, format);
        }
        updatePanel();
    }

    // Represent the action taken after the user provides reference number
    private class PromptReferenceListener implements ActionListener {
        private String promptAction;

        PromptReferenceListener(String getAction) {
            super();
            promptAction = getAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int reference = Integer.parseInt(referenceField.getText());
            try {
                if (reference < 0 || reference >= user.numberOfRides()) {
                    throw new OutOfBoundInput();
                } else {
                    handleAction(promptAction, reference);
                }
            } catch (OutOfBoundInput | ReviewedRideException ex) {
                removeComponents();
                JLabel wrongInput = new JLabel("A invalid reference number was received.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            } catch (RideCannotBeCancelled ex2) {
                removeComponents();
                JLabel wrongInput = new JLabel("RIDES SERVED BY DRIVERS OUTSIDE THE STARTING ZONE "
                        + "CANNOT BE CANCELLED.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            }
        }
    }

    // REQUIRES: 0 <= reference < number of rides in the list, action == "cancel" || action == "rating"
    // EFFECTS: handle the reference provided by the user according to the action specified
    private void handleAction(String action, int reference) throws RideCannotBeCancelled, ReviewedRideException {
        if (action.equals("cancel")) {
            cancellation(reference);
        } else if (action.equals("rating")) {
            doRating(reference);
        }
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: cancels the given ride from the user
    private void cancellation(int reference) throws RideCannotBeCancelled, ReviewedRideException {
        int cancelDriver = user.getDriverOfRide(reference);
        int start = user.getStartOfRide(reference);
        int end = user.getEndOfRide(reference);
        int time = user.getTimeOfRide(reference);
        int duration = abs(start - end) + 1;
        kingdom.cancellation(cancelDriver, time, duration, reference);
        removeComponents();
        JLabel cancelled = new JLabel("Booking is cancelled, we hope to see you again :)");
        outputPanel.add(cancelled, format);
        updatePanel();
    }

    // REQUIRES: 0 <= reference < number of rides in the list
    // EFFECTS: prompts the user for the rank of the driver and do the rating.
    private void doRating(int reference) {
        int driverOfRide = user.getDriverOfRide(reference);
        JLabel rate = new JLabel("Rank from 0 - 5: ");
        outputPanel.add(rate, format);
        ratingField = new JTextField(10);
        outputPanel.add(ratingField, format);
        JButton submit = new JButton("Process");
        submit.addActionListener(new RateListener(reference, driverOfRide));
        outputPanel.add(submit, format);
        updatePanel();
    }

    // Represent the action taken when user wants to rate the drivers of his/her ride
    private class RateListener implements ActionListener {
        private int reference;
        private int driverOfRide;

        RateListener(int reference, int driverOfRide) {
            super();
            this.reference = reference;
            this.driverOfRide = driverOfRide;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            double rating = Double.parseDouble(ratingField.getText());
            try {
                if (rating < 0 || rating > 5) {
                    throw new OutOfBoundInput();
                }
                kingdom.rateDriver(reference, rating, driverOfRide);
                removeComponents();
                JLabel thanks = new JLabel("Thank you for your advice :)");
                outputPanel.add(thanks, format);
                updatePanel();
            } catch (OutOfBoundInput | ReviewedRideException ex) {
                removeComponents();
                JLabel wrongInput = new JLabel("Rating not processed, please rate from 0 to 5.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            }
        }
    }

    // EFFECTS: prompts the user for time, origin, destination of the ride for booking.
    private void promptInputForBooking() {
        JLabel timeLabel = new JLabel("Please enter the time of your appointment(0-23): ");
        outputPanel.add(timeLabel, format);
        timeField = new JTextField(10);
        outputPanel.add(timeField, format);
        JLabel startLabel = new JLabel("Please enter the zone of your starting point(1 - 5): ");
        outputPanel.add(startLabel, format);
        startField = new JTextField(10);
        outputPanel.add(startField, format);
        JLabel endLabel = new JLabel("Please enter the zone of your destination(1 - 5): ");
        outputPanel.add(endLabel, format);
        endField = new JTextField(10);
        outputPanel.add(endField, format);
        JButton submit = new JButton("Process");
        submit.addActionListener(new BookingListener());
        outputPanel.add(submit, format);
        updatePanel();
    }

    // Represent the action taken when the user wants to book a ride
    private class BookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int time = Integer.parseInt(timeField.getText());
            int start = Integer.parseInt(startField.getText());
            int destination = Integer.parseInt(endField.getText());
            int duration = abs(start - destination) + 1;
            try {
                if (time > 23 || time < 0 || start > 5 || start < 1 || destination > 5 || destination < 1) {
                    throw new OutOfBoundInput();
                }
                booking(time, start, destination, duration);
            } catch (OutOfBoundInput ex) {
                removeComponents();
                JLabel wrongInput = new JLabel("An invalid number.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            } catch (DriversOffWork ex) {
                removeComponents();
                JLabel wrongInput = new JLabel("Sorry, our drivers off at 23:00, "
                        + "we cannot provide service later than that.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            }
        }
    }

    // REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5
    // EFFECTS: adds a booking to the customer serving.
    private void booking(int time, int start, int destination, int duration) throws DriversOffWork {
        List<String> driversAvailable = kingdom.getDriversWithinZone(time, start, duration);
        if (driversAvailable.isEmpty()) {
            addFeeBooking(time, start, destination, duration);
        } else {
            choosingDriver(driversAvailable, start, time, destination);
        }
    }

    // REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5
    // EFFECTS: adds booking if there's no drivers in the starting zone.
    private void addFeeBooking(int time, int start, int destination, int duration) {
        removeComponents();
        JLabel noDriver = new JLabel("There's no driver available in the zone at that time. ");
        outputPanel.add(noDriver, format);
        JLabel promptChoice = new JLabel("Would you like to choose drivers from other zones?");
        outputPanel.add(promptChoice, format);
        ButtonGroup yesOrNo = new ButtonGroup();
        yes = new JRadioButton("yes");
        yesOrNo.add(yes);
        JRadioButton no = new JRadioButton("no");
        yesOrNo.add(no);
        outputPanel.add(yes);
        outputPanel.add(no, format);
        JButton submit = new JButton("Process");
        submit.addActionListener(new RadioButtonListener(time, start, duration, destination));
        outputPanel.add(submit, format);
        updatePanel();
    }

    // REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5
    // EFFECTS: if the user chooses yes: book a ride with additional fee
    //          if the user chooses no: do nothing
    private void handleRadioButton(JRadioButton yes, int time, int start, int duration, int destination)
            throws DriversOffWork {
        if (yes.isSelected()) {
            List<String> driversAvailable = kingdom.getDriversOutOfZone(time, start, duration);
            if (driversAvailable.isEmpty()) {
                removeComponents();
                JLabel sorry = new JLabel("Sorry, there's no other drivers available at that time,"
                        + " we hope to see you again.");
                outputPanel.add(sorry, format);
                updatePanel();
            } else {
                choosingDriver(driversAvailable, start, time, destination);
            }
        } else {
            removeComponents();
            JLabel sorry = new JLabel("We apologize for the inconvenience.");
            outputPanel.add(sorry, format);
            updatePanel();
        }
    }

    // Represent the action taken when the user uses the radio button
    private class RadioButtonListener implements ActionListener {
        private int time;
        private int start;
        private int duration;
        private int destination;

        RadioButtonListener(int time, int start, int duration, int destination) {
            super();
            this.time = time;
            this.start = start;
            this.duration = duration;
            this.destination = destination;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                handleRadioButton(yes, time, start, duration, destination);
            } catch (DriversOffWork ex) {
                removeComponents();
                JLabel wrongInput = new JLabel("Sorry, our drivers off at 23:00, "
                        + "we cannot provide service later than that.");
                outputPanel.add(wrongInput, format);
                updatePanel();
            }
        }
    }

    // REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5
    // EFFECTS: prints the given list of drivers and prompts the user to choose a driver.
    private void choosingDriver(List<String> driversAvailable, int start, int time, int destination) {
        removeComponents();
        outputPanel.add(new JList(driversAvailable.toArray()), format);
        JLabel promptDriver = new JLabel("Please choose the drivers from above, enter the number of the driver: ");
        outputPanel.add(promptDriver, format);
        driverField = new JTextField(10);
        outputPanel.add(driverField, format);
        JButton submit = new JButton("Process");
        submit.addActionListener(new ChooseDriverListener(time, start, destination));
        outputPanel.add(submit, format);
        updatePanel();
    }

    // Represent the action taken when the user needs to choose drivers from other zones
    private class ChooseDriverListener implements ActionListener {
        private int time;
        private int start;
        private int destination;

        ChooseDriverListener(int time, int start, int destination) {
            super();
            this.time = time;
            this.start = start;
            this.destination = destination;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int chosenDriver = Integer.parseInt(driverField.getText());
            try {
                if (chosenDriver >= kingdom.numberOfDrivers() || chosenDriver < 0) {
                    throw new OutOfBoundInput();
                }
                int driverZone = kingdom.getDriverZone(chosenDriver);
                receipt(time, start, destination, chosenDriver, abs(driverZone - start));
            } catch (OutOfBoundInput ex) {
                JLabel wrongInput = new JLabel("Booking failed, you did not enter a valid reference number.");
                removeComponents();
                outputPanel.add(wrongInput, format);
                updatePanel();
            }
        }
    }

    // REQUIRES: 0 <= time <= 22, 1 <= start <= 5, 1 <= destination <= 5, 0 <= chosenDriver < number of drivers created
    // MODIFIES: ask the Company to book a ride and print the receipt.
    // EFFECTS: a booking is made
    private void receipt(int time,int start,int destination,int chosenDriver,int additional) {
        removeComponents();
        int cost = kingdom.addRide(time, start, destination, chosenDriver, additional);
        String receipt = "The cost of the ride is $ " + cost
                + ", our driver will contact you soon.";
        JLabel outReceipt = new JLabel(receipt);
        removeComponents();
        outputPanel.add(outReceipt, format);
        updatePanel();
    }

    // represent the action to be taken when the user wants to see the price list
    private class PrintPrice extends AbstractAction {

        PrintPrice() {
            super("View Price List");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            String[] price = new String[4];
            price[0] = "NOTE: RIDES SERVED BY DRIVERS OUTSIDE THE STARTING ZONE CANNOT BE CANCELLED. ";
            price[1] = "Ride within 1 zone   - $" + kingdom.getOneZoneCost();
            price[2] = "Additional cost for ride between zones   - +$" + kingdom.getAdditionalFee() + " /zone";
            price[3] = "Additional cost for booking driver outside the starting zone   - +$"
                    + kingdom.getAdditionalFee() + " /zone";

            removeComponents();
            outputPanel.add(new JList(price), format);
            updatePanel();
        }
    }

    // Represent the action taken when the user wants to rate the driver
    private class RateDrivers extends AbstractAction {

        RateDrivers() {
            super("Rate Our Drivers");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            removeComponents();
            promptReferenceNumber("rating");
        }
    }

    // Represent the action taken when the user wants to cancel booking
    private class Cancel extends AbstractAction {

        Cancel() {
            super("Cancel Rides");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            removeComponents();
            promptReferenceNumber("cancel");
        }
    }

    // Represent the action taken when the user wants to make a booking
    private class Booking extends AbstractAction {

        Booking() {
            super("Book Rides");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            removeComponents();
            promptInputForBooking();
        }
    }

    /* ----------------------------Actions---------------------------- */

    // starts the application
    public static void main(String[] args) {
        new ServiceInterface();
    }
}
