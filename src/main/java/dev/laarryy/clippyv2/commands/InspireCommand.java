package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.util.ChannelUtil;
import dev.laarryy.clippyv2.util.RoleUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.channel.Channel;
import java.awt.*;
import java.io.IOException;


public class InspireCommand implements CommandExecutor {

    private static final OkHttpClient client = new OkHttpClient();

    @Command(aliases = {"!inspireme", "!inspiration", ".inspireme", ".inspiration"}, usage = "!inspireme", description = "Inspires you!")
    public String onCommand(Server server, User user, Message message, Channel channel) throws IOException {
        if (ChannelUtil.isNonPublicChannel(channel) || ChannelUtil.isOffTopic(channel)|| RoleUtil.isStaff(user, server)) {
        EmbedBuilder inspiration = new EmbedBuilder();
            String url = "https://inspirobot.me/api?generate=true";
            Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) return null;
            String content = response.body().string();
            response.body().close();
            inspiration.setImage(content);
            inspiration.setColor(new Color(0x09E214));
            inspiration.setFooter("For "  + user.getName());
            message.getChannel().sendMessage(inspiration);
            response.close();
        } else {
            message.addReaction("\uD83D\uDEAB");
        } return null;
    }
}




