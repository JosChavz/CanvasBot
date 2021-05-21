import java.util.ArrayList;
import java.util.HashMap;

public class Cache {
    public static HashMap<String, Assignment> cache;

    public Cache() {
        cache = new HashMap<>();
    }

    public static void setCache(ArrayList<Assignment> assignments) {
        for(Assignment assignment : assignments) {
            cache.put(assignment.getName(), assignment);
        }
    }

    // CREATE A CACHE CLEAN SYSTEM EVERY HOUR
    public static void cleanCache() {
        // There are assignments due today - Check time
        int todayHour = Integer.parseInt(CanvasAPI.todayTimeArr[0]);

        for(String key : cache.keySet()) {
            Assignment tempAssignment = cache.get(key);
            String due = tempAssignment.getDueDateTime();
            // Assignment Due Time
            String[] assignmentTime = due.split(":");
            int assignmentHour = Integer.parseInt(assignmentTime[0]);
            // Removes the assignment the same hour or less than Today's Hour
            if(assignmentHour <= todayHour && tempAssignment.getHasPublished()) {
                System.out.println(ConsoleColors.RED_BOLD + "Removed [" + tempAssignment.getName() + "] that was due on " +
                        tempAssignment.getDueDateTime() + " UTC" + ConsoleColors.RESET);
                cache.remove(key);
            }
        }
    }
}
