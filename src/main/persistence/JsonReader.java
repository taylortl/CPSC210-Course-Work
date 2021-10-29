package persistence;

import model.Company;
import model.Customer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
// Represents a reader that reads customer from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads customer from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Company read() throws IOException {
        String jsonData = readFile(source);
        JSONObject companyJson = new JSONObject(jsonData);
        return parseCompany(companyJson);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses Company from JSON object and returns it
    private Company parseCompany(JSONObject companyJson) {
        JSONObject customerJson = companyJson.getJSONObject("customer");
        Customer customer = new Customer();
        Company company = new Company(customer);
        addRides(company, customerJson);
        return company;
    }

    // MODIFIES: company
    // EFFECTS: parses rides from JSON object and adds them to company
    private void addRides(Company company, JSONObject customerJson) {
        JSONArray jsonArray = customerJson.getJSONArray("rides");
        for (Object json : jsonArray) {
            JSONObject rideJson = (JSONObject) json;
            addRide(company, rideJson);
        }
    }

    // MODIFIES: company
    // EFFECTS: parses ride from JSON object and adds it to company
    private void addRide(Company company, JSONObject rideJson) {
        int driverNum = rideJson.getInt("driver");
        int time = rideJson.getInt("startTime");
        int startZone = rideJson.getInt("startZone");
        int destination = rideJson.getInt("destination");
        int additionalFee = rideJson.getInt("additionalFee");
        boolean reviewed = rideJson.getBoolean("reviewed");
        company.addOldRide(time, startZone, destination, driverNum, additionalFee, reviewed);
    }

}
