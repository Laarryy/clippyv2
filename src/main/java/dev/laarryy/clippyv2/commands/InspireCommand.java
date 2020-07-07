package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.Command;
import dev.laarryy.clippyv2.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channel;


public class InspireCommand implements CommandExecutor {

    private final String url = "https://inspirobot.me/api?generate=true";
    private static final OkHttpClient client = new OkHttpClient();

    @Command(aliases = {"!inspireme", "!inspiration", ".inspireme", ".inspiration"}, usage = "!inspireme", description = "Inspires you!")
    public String onCommand(Server server, String[] args, User user, Message message, Channel channel) throws IOException {
        EmbedBuilder inspiration = new EmbedBuilder();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) return null;
        if (message.getChannel().getIdAsString().equals(Constants.CHANNEL_OFFTOPIC)
                || message.getChannel().getIdAsString().equals(Constants.CHANNEL_PATREONS)
                || message.getChannel().getIdAsString().equals(Constants.CHANNEL_HELPFUL)
                || message.getChannel().getIdAsString().equals(Constants.CHANNEL_STAFF)) {
            String content = response.body().string();
            response.body().close();
            message.getChannel().sendMessage(content);
            response.close();
        }
        return null;
    }
}



