import java.time.Duration;

import io.timeandspace.cronscheduler.CronScheduler;
import discord4j.core.GatewayDiscordClient;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Cron {
  private GatewayDiscordClient bot;
  private CanvasAPI api;
  public Cron(GatewayDiscordClient bot, CanvasAPI api) {
    this.bot = bot;
    this.api = api;

    assignmentsCleanUp();
    assignmentCronTask();
    dailyAssignmentTask();
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

      if(todaysAssigments != null && !todaysAssigments.isEmpty()) {
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
        boolean channelSet = !(DiscordMain.announcementChannel.asString().isEmpty());
        if(!message.isEmpty() && channelSet)
          bot.getChannelById(DiscordMain.announcementChannel).block().getRestChannel().createMessage("@everyone" + message).block();
      } // OUT OF IF-STATEMENT
    });
  }

  private void assignmentsCleanUp() {
    System.out.println(ConsoleColors.GREEN_BACKGROUND + ConsoleColors.BLACK + "Doing hourly Cache cleaning..." + ConsoleColors.RESET);
    Duration syncPeriod = Duration.ofHours(1);
    CronScheduler cron = CronScheduler.create(syncPeriod);
    cron.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.HOURS, runTimeMillis -> {
      Cache.cleanCache();
    });
  }

  private void dailyAssignmentTask() {
    Duration syncPeriod = Duration.ofHours(24);
    CronScheduler cron = CronScheduler.create(syncPeriod);
    cron.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.HOURS, runTimeMillis -> {
      String message = "";
      // will call the API here
      ArrayList<Assignment> todaysAssigments= api.getTomorrowAssignments();
      // There are assignments due today - Check time
      int todayHour = Integer.parseInt(CanvasAPI.todayTimeArr[0]);

      if(todaysAssigments != null && !todaysAssigments.isEmpty()) {
        for(Assignment assignment : todaysAssigments) {
          String due = assignment.getDueDate();
          String assignmentName = assignment.getName();
          // System.out.println("Assignment name: " + assignmentName);

          // Checks to see if the current hour is an hour before the assignment's time
          if(!assignment.getHasPublished()) {
            message = "\n**" + assignmentName + "** due **__tomorrow__** at " + assignment.correctTimeZoneDueDateTime + "!";
            assignment.publish();
          }
          //bot.getChannelById(id).block().getRestChannel().createMessage(assignmentName + " due in an hour!").block();
        } // OUT OF FOR-LOOP
        // bot.getGuildById(Snowflake.of("")).block().getChannelById(Snowflake.of("")); flexible way when more servers?
        boolean channelSet = !(DiscordMain.announcementChannel.asString().isEmpty());
        if(!message.isEmpty() && channelSet)
          bot.getChannelById(DiscordMain.announcementChannel).block().getRestChannel().createMessage("@everyone" + message).block();
      } // OUT OF IF-STATEMENT
    });
  }
}
