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
import java.util.HashMap;
import java.util.function.Consumer;

public class DiscordMain {
  public static Snowflake announcementChannel = Snowflake.of("839572172031655936");

  public static void main(String[] args) {
    HashMap<String, String> keyMap = initKeys();
    // TEMP, DB NEEDS TO BE IMPLEMENTED SOON
    HashMap<Guild, Snowflake> guildsAdmin = new HashMap<>();
    
    // Initializing Discord Bot
    GatewayDiscordClient client = DiscordClientBuilder.create(keyMap.get("discord_key"))
      .build()
      .login()
      .block();

    // Event listener when bot is invited to a server
    client.on(GuildCreateEvent.class).subscribe(event -> {
      Guild guild = event.getGuild();
      guildsAdmin.put(guild, guild.getOwner().block().getId());
    });

    CanvasAPI api = new CanvasAPI(keyMap.get("canvas_key"), keyMap.get("url"));
    new Cache();
    new Cron(client, api);

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

    client.onDisconnect().block();
  }

  public static HashMap<String, String> initKeys() {
    HashMap<String, String> keyMap = new HashMap<>();

    try {
      Wini ini = new Wini(new File("src/main/resources/keys.ini"));
      keyMap.put("discord_key", ini.get("api-keys", "discord_key"));
      keyMap.put("canvas_key", ini.get("api-keys", "canvas_key"));
      keyMap.put("url", ini.get("site-url", "url"));
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
