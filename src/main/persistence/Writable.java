package persistence;

import org.json.JSONObject;

/******************************************
 *    Title: JsonSerializationDemo
 *    Author: Paul Carter
 *    Date: 2021-03-07
 *    Location: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 *
 ******************************************/
// Represents an interface that can be converted to Json object
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
