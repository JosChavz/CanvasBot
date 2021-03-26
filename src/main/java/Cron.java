import java.time.Duration;

import discord4j.common.util.Snowflake;
import io.timeandspace.cronscheduler.CronScheduler;
import discord4j.core.GatewayDiscordClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Cron {
  private GatewayDiscordClient bot;
  private CanvasAPI api;
  public Cron(GatewayDiscordClient bot, CanvasAPI api) {
    this.bot = bot;
    this.api = api;

    assignmentCronTask();
    assignmentsCleanUp();
  }

  private void assignmentCronTask() {
    Duration syncPeriod = Duration.ofMinutes(1);
    CronScheduler cron = CronScheduler.create(syncPeriod);
    cron.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.MINUTES, runTimeMillis -> {
      String message = "";
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
            message = "\n**" + assignmentName + "** due in an hour at " + assignment.correctTimeZoneDueDateTime + "!";
            assignment.publish();
          }
          //bot.getChannelById(id).block().getRestChannel().createMessage(assignmentName + " due in an hour!").block();
        } // OUT OF FOR-LOOP
        // bot.getGuildById(Snowflake.of("")).block().getChannelById(Snowflake.of("")); flexible way when more servers?
        boolean channelSet = !(Main.announcementChannel.asString().isEmpty());
        if(!message.isEmpty() && channelSet)
          bot.getChannelById(Main.announcementChannel).block().getRestChannel().createMessage("@everyone" + message).block();
      } // OUT OF IF-STATEMENT

    });
  }

  private void assignmentsCleanUp() {
    Duration syncPeriod = Duration.ofHours(1);
    CronScheduler cron = CronScheduler.create(syncPeriod);
    cron.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.HOURS, runTimeMillis -> {
      Cache.cleanCache();
    });
  }
}
