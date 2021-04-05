import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.Message;
import java.io.File;

import discord4j.core.spec.RoleCreateSpec;
import discord4j.rest.util.Color;
import org.ini4j.*;

import java.sql.*;
import java.util.HashMap;
import java.util.function.Consumer;

public class DiscordMain {
  public static Snowflake announcementChannel;
  public static Database db;

  public static void main(String[] args) {
    HashMap<String, String> keyMap = initKeys();
    db = new Database(keyMap.get("db-url"), keyMap.get("db-user"), keyMap.get("db-pass"));
    
    // Initializing Discord Bot
    GatewayDiscordClient client = DiscordClientBuilder.create(keyMap.get("discord_key"))
      .build()
      .login()
      .block();

    // Event listener when bot is invited to a server
    client.on(GuildCreateEvent.class).subscribe(event -> {
      Guild guild = event.getGuild();

      try {
        db.connect();
        db.logKey(guild.getId().asString(), guild.getOwner().block().getId().asString());
        db.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

    });

    //CanvasAPI api = new CanvasAPI(keyMap.get("canvas_key"), keyMap.get("url"));
    //new Cache();
    //new Cron(client, api);

    client.getEventDispatcher().on(MessageCreateEvent.class)
        .map(MessageCreateEvent::getMessage)
        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
        .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
        .flatMap(Message::getChannel)
        .flatMap(channel -> channel.createMessage("Pong!"))
        .subscribe();

    client.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      final Snowflake guildOwnerID = event.getGuild().block().getOwner().block().getId();
      final Snowflake authorID = message.getAuthor().get().getId();

      if ("!announceHere".equals(message.getContent()) && authorID.equals(guildOwnerID)) {
        MessageChannel channel = message.getChannel().block();

        announcementChannel = channel.getId();


        channel.createMessage("Announcements will be set here!").block();
      }
    });

    client.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      final String[] text = message.getContent().split(" ");
      MessageChannel channel = message.getChannel().block();

      if ("!setCanvas".contains(text[0])) {

        if(text.length == 1) channel.createMessage("Instructions...").block();
        else if(text.length == 2) {
          channel.createMessage("Let\'s check if valid API key...").block();

          try {
            db.connect();
            // Gets the URL of the user's Canvas
            String canvasUrl = db.getCanvasUrl(message.getGuildId().get().asString());
            if(!canvasUrl.isEmpty()) {
              // Checks to see if the API key is valid
              CanvasAPI.checkApiKey(text[1], canvasUrl);
            } else {
              channel.createMessage("Make sure to have a URL first. Use `!setUrl`.").block();
            }
            db.close();
          } catch (SQLException throwables) {
            throwables.printStackTrace();
          }


        }
        else
          channel.createMessage("Sorry, I did not pick that up... please send it as\n"
          + "`!setCanvas abc123321avc-abc`").block();
      }

    });

    client.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      final Snowflake guildOwnerID = event.getGuild().block().getOwner().block().getId();
      final Snowflake authorID = message.getAuthor().get().getId();
      final String[] text = message.getContent().split(" ");

      if ("!setUrl".equals(text[0]) && authorID.equals(guildOwnerID)) {
        MessageChannel channel = message.getChannel().block();

        // Which means there was an argument passed
        if(text.length == 2) {
          if(text[1].contains("https://") && text[1].contains(".instructure.com")) {
            String tempUrl = text[1];
            // Last character isn't /
            if(!(tempUrl.charAt(tempUrl.length() - 1) == '/')) tempUrl += "/";

            try {
              db.connect();
              db.setCanvasUrl(message.getGuildId().get().asString(), tempUrl);
              channel.createMessage("Okay! 👍 Updated!").block();
              db.close();
            } catch (SQLException throwables) {
              throwables.printStackTrace();
            }
          }
          else channel.createMessage("Please add the full URL\nex: `https://miracosta.instructure.com/`").block();
        } // OUT OF IF STATEMENT
      } // OUT OF GENERAL IF STATEMENT
    });

    /** FOR DEVELOPMENT PURPOSES **/
    client.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();

      if ("!dev".equals(message.getContent())) {
        MessageChannel channel = message.getChannel().block();
        String guildId = message.getGuildId().get().asString();



        channel.createMessage("dev").block();
      }
    });

    client.onDisconnect().block();
  }

  public static HashMap<String, String> initKeys() {
    HashMap<String, String> keyMap = new HashMap<>();

    try {
      Wini ini = new Wini(new File("src/main/resources/keys.ini"));
      keyMap.put("discord_key", ini.get("api-keys", "discord_key"));
      keyMap.put("canvas_key", ini.get("api-keys", "canvas_key"));
      keyMap.put("url", ini.get("site-url", "url"));
      keyMap.put("db-url", ini.get("database", "db-url"));
      keyMap.put("db-user", ini.get("database", "db-user"));
      keyMap.put("db-pass", ini.get("database", "db-pass"));
      // To catch basically any error related to finding the file e.g
      // (The system cannot find the file specified)
    }catch(Exception e){
      System.err.println(e.getMessage());
      // If keys cannot successfully be retrieved, then the 
      // whole program will crash regardless
      System.exit(0);
    }

    return keyMap;
  }

}
