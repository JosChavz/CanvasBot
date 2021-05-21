import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

public class CanvasAPI {
    private String key;
    private String url;
    private String todayDate;
    public static String[] todayDateArr;
    private String todayTime;
    public static String[] todayTimeArr;

    public CanvasAPI(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public void initializeTime() {
        // Date
        TimeZone.setDefault( TimeZone.getTimeZone("UTC")); // SET SYSTEM TIME TO UTC
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        this.todayDate = dtf.format(now);

        todayDateArr = this.todayDate.split("-");
        // Time - UTC
        Clock clock = Clock.systemUTC();
        LocalTime nowTime = LocalTime.now(clock);
        todayTime = nowTime.format( DateTimeFormatter.ofPattern("HH:mm:ss") );

        todayTimeArr = todayTime.split(":");
    }

    public ArrayList<Assignment> getTodaysAssigments() {
        boolean newAssignments = true;
        HttpResponse<JsonNode> jsonResponse = null;
        ArrayList<Assignment> todaysAssignmentsArr = new ArrayList<>();

        // initializes new time
        initializeTime();

        System.out.println(ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + "TODAY - API called at (UTC):" + ConsoleColors.RESET);
        System.out.println(todayDate);
        System.out.println(todayTime);

        // Status - Debugging purposes
        // System.out.println(jsonResponse.getBody());
        // 200 - Success
        //System.out.println(jsonResponse.getStatus());

        try {
            jsonResponse = Unirest.get(this.url)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .queryString("access_token", this.key)
                    .queryString("bucket", "future")
                    .asJson();

            JSONArray responseJSONString = jsonResponse.getBody().getArray();

            // dateArr - current assignment due date
            // todayDateArr - today's date STATIC
            // YEAR - MONTH - DAY
            for(int i = 0; i < responseJSONString.length(); i++ ) {
                //System.out.println(responseJSONString.get(i));
                // CanvasAPI returns an array of JSON Objects
                // So we get the i-position object in array
                // and parse into a JSONObject
                JSONObject JSONDate = responseJSONString.getJSONObject(i);

                // Skips if due date is null
                if(JSONDate.isNull("due_at") || JSONDate.isNull("name") ) continue;

                // Creates Assignment object
                Assignment tempAssignment = new Assignment(JSONDate);

                // Check current assignment in cache
                if(!Cache.cache.isEmpty()) {
                    // If Cache has the key of current assignment's name - meaning it exists
                    if(Cache.cache.containsKey(tempAssignment.getName())
                            && Cache.cache.get(tempAssignment.getName()).equals(tempAssignment)) {
                        System.out.println("CONTAINS THE SAME");
                        newAssignments = false;
                        continue;
                    }
                    else newAssignments = true;
                }

                // Assignment Due Date and Time
                // System.out.println( "Current iteration assignment due at: " + JSONDate.get("due_at"));
                String date = tempAssignment.getDueDate();

                // Assignment Due Date
                String[] dateArr = date.split("-");

                // Checks if the due date is the same year, month, and day
                if(dateArr[1].equals(todayDateArr[1])
                        && dateArr[0].equals(todayDateArr[0])
                        && dateArr[2].equals(todayDateArr[2])) {
                    System.out.println( ConsoleColors.YELLOW_BOLD + "AN ASSIGNMENT IS DUE TODAY!" + ConsoleColors.RESET);
                    todaysAssignmentsArr.add(tempAssignment);
                } // OUT OF IF-STATEMENT

            } // OUT OF FOR-LOOP
            // Assign cache
            if(newAssignments) Cache.setCache(todaysAssignmentsArr);

            return todaysAssignmentsArr;
        } catch(Exception e) {
            System.out.println("Something went wrong...");
            //System.exit(0);
        }

        return null;
    }

    public ArrayList<Assignment> getTomorrowAssignments() {
        boolean newAssignments = true;
        HttpResponse<JsonNode> jsonResponse = null;
        ArrayList<Assignment> todaysAssignmentsArr = new ArrayList<>();

        // initializes new time
        initializeTime();

        System.out.println(ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + "TOMORROW - API called at (UTC):" + ConsoleColors.RESET);
        System.out.println(todayDate);
        System.out.println(todayTime);

        // Status - Debugging purposes
        // System.out.println(jsonResponse.getBody());
        // 200 - Success
        //System.out.println(jsonResponse.getStatus());

        try {
            jsonResponse = Unirest.get(this.url)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .queryString("access_token", this.key)
                    .queryString("bucket", "future")
                    .asJson();

            JSONArray responseJSONString = jsonResponse.getBody().getArray();

            // dateArr - current assignment due date
            // todayDateArr - today's date STATIC
            // YEAR - MONTH - DAY
            for(int i = 0; i < responseJSONString.length(); i++ ) {
                //System.out.println(responseJSONString.get(i));
                // CanvasAPI returns an array of JSON Objects
                // So we get the i-position object in array
                // and parse into a JSONObject
                JSONObject JSONDate = responseJSONString.getJSONObject(i);

                // Skips if due date is null
                if(JSONDate.isNull("due_at") || JSONDate.isNull("name") ) continue;

                // Creates Assignment object
                Assignment tempAssignment = new Assignment(JSONDate);

                // Check current assignment in cache
                if(!Cache.cache.isEmpty()) {
                    // If Cache has the key of current assignment's name - meaning it exists
                    if(Cache.cache.containsKey(tempAssignment.getName())
                            && Cache.cache.get(tempAssignment.getName()).equals(tempAssignment)) {
                        System.out.println("CONTAINS THE SAME");
                        newAssignments = false;
                        continue;
                    }
                    else newAssignments = true;
                }

                // Assignment Due Date and Time
                 System.out.println( "Current iteration assignment due at: " + JSONDate.get("due_at"));
                String date = tempAssignment.getDueDate();

                // Assignment Due Date
                String[] dateArr = date.split("-");

                // Checks if the due date is the same year, month, and day
                if(dateArr[1].equals(todayDateArr[1])
                        && dateArr[0].equals(todayDateArr[0])
                        && dateArr[2].equals(
                        // Returns Today's Day + 1 (Assuming it's tomorrow in this case)
                        String.valueOf(Integer.parseInt(todayDateArr[2]) + 1)
                )) {
                    System.out.println( ConsoleColors.YELLOW_BOLD + "AN ASSIGNMENT IS DUE TOMORROW!" + ConsoleColors.RESET);
                    todaysAssignmentsArr.add(tempAssignment);
                } // OUT OF IF-STATEMENT

            } // OUT OF FOR-LOOP
            // Assign cache
            if(newAssignments) Cache.setCache(todaysAssignmentsArr);

            return todaysAssignmentsArr;
        } catch(Exception e) {
            System.out.println("Something went wrong...");
            //System.exit(0);
        }

        return null;
    }
}
