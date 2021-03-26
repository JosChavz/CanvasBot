import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.Message;
import java.io.File;
import org.ini4j.*;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) {
    HashMap<String, String> keyMap = initKeys();
    
    // Initializing Discord Bot
    GatewayDiscordClient client = DiscordClientBuilder.create(keyMap.get("discord_key"))
      .build()
      .login()
      .block();

    CanvasAPI api = new CanvasAPI(keyMap.get("canvas_key"), keyMap.get("url"));
    new Cache();
    new Cron(client, api);

    assert client != null;
    client.getEventDispatcher().on(MessageCreateEvent.class)
        .map(MessageCreateEvent::getMessage)
        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
        .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
        .flatMap(Message::getChannel)
        .flatMap(channel -> channel.createMessage("Pong!"))
        .subscribe();

    client.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      if ("!channelID".equals(message.getContent())) {
        MessageChannel channel = message.getChannel().block();
        assert channel != null;
        channel.createMessage(String.valueOf(channel.getId())).block();
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
