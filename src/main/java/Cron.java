import java.time.Duration;

import discord4j.common.util.Snowflake;
import io.timeandspace.cronscheduler.CronScheduler;
import discord4j.core.GatewayDiscordClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Cron {
  public Cron(GatewayDiscordClient bot, CanvasAPI api) {
    Snowflake id = Snowflake.of("820142600899133510"); // The channel's ID
    Duration syncPeriod = Duration.ofMinutes(1);
    CronScheduler cron = CronScheduler.create(syncPeriod);
    cron.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.MINUTES, runTimeMillis -> {
      // will call the API here
      ArrayList<Assignment> todaysAssigments= api.getTodaysAssigments();
      // There are assignments due today - Check time
      int todayHour = Integer.parseInt(CanvasAPI.todayTimeArr[0]);

      if(todaysAssigments != null) {
        for(Assignment assignment : todaysAssigments) {
          String due = assignment.getDueDateTime();
          String assignmentName = assignment.getName();
          // System.out.println("Assignment name: " + assignmentName);

          // Assignment Due Time
          String[] assignmentTime = due.split(":");
          int assignmentHour = Integer.parseInt(assignmentTime[0]);
          // System.out.println("assignment hour" + assignmentHour);

          // Checks to see if the current hour is an hour before the assignment's time
          if(assignmentHour - 1 == todayHour && !assignment.getHasPublished()) {
            bot.getChannelById(id).block().getRestChannel().createMessage(assignmentName + " due in an hour at "
                    + assignment.correctTimeZoneDueDateTime + "!").block();
            assignment.publish();
          }
            //bot.getChannelById(id).block().getRestChannel().createMessage(assignmentName + " due in an hour!").block();
        } // OUT OF FOR-LOOP
      } // OUT OF IF-STATEMENT

    }); 
  }
}
