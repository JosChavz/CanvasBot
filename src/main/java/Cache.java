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
}
