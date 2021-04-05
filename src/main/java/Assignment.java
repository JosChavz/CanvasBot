import org.json.JSONObject;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class Assignment {
    private String name;
    private String dueDate;
    private String dueDateTime;
    public String correctTimeZoneDueDateTime;
    public String correctTimeZoneDueDate;
    private boolean hasPublished;
    private boolean isForTomorrow;

    public Assignment(JSONObject json) {
        this.name = json.getString("name");
        String date_time = json.getString("due_at");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime localtDateAndTime = LocalDateTime.parse(date_time, formatter);
        ZonedDateTime utcTime = ZonedDateTime.of(localtDateAndTime, ZoneId.of("UTC"));
        // Changes UTC to User's Time Zone
        ZoneId cali = ZoneId.of("America/Los_Angeles"); // has to be changed for the flexibility of users
        ZonedDateTime pacificTime = utcTime.withZoneSameInstant(cali); // has to be changed for the flexibility of users
        this.correctTimeZoneDueDateTime = pacificTime.format(DateTimeFormatter.ofPattern("K:mm")); // K for US, HH for other.. ask for 24hr or 12?
        this.correctTimeZoneDueDate = pacificTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.dueDate = date_time.substring(0, date_time.indexOf('T'));
        this.dueDateTime = date_time.substring(date_time.indexOf('T') + 1);
        this.hasPublished = false;
        this.isForTomorrow = false;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public boolean getHasPublished() {
        return hasPublished;
    }

    public void publish() {
        this.hasPublished = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(name, that.name) && Objects.equals(dueDate, that.dueDate) && Objects.equals(dueDateTime, that.dueDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dueDate, dueDateTime);
    }

    public void setForTomorrow(boolean forTomorrow) {
        isForTomorrow = forTomorrow;
    }
}
