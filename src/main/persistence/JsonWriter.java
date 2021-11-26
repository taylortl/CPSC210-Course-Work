package persistence;

import model.Company;
import model.Customer;
import model.Event;
import model.EventLog;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
// Represents a writer that writes JSON representation of company to file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String filename;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String filename) {
        this.filename = filename;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(filename));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of company to file
    public void write(Company company) {
        JSONObject companyJson = company.toJson();
        saveToFile(companyJson.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
        EventLog.getInstance().logEvent(new Event("Ride history saved"));
    }


}
