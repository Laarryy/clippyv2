package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.util.ChannelUtil;
import dev.laarryy.clippyv2.util.EmbedUtil;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class EmbedCommand implements CommandExecutor {

    EmbedUtil embedUtil = new EmbedUtil();

    @Command(aliases = {"!embed", ".embed"}, usage = "!embed <url>", description = "Makes an embed from a json text")
    public void onCommand(String[] args, User user, TextChannel channel, MessageAuthor messageAuthor, Server server, Message message) {
        if (!(RoleUtil.isStaff(user, server) || ChannelUtil.isNonPublicChannel(channel) || ChannelUtil.isOffTopic(channel))) {
            message.addReaction("\uD83D\uDEAB");
            return;
        }
        if (args.length == 0) {
            channel.sendMessage(new EmbedBuilder().setTitle("Invalid URL").setColor(Color.RED));
            return;
        }
        if (RoleUtil.isStaff(user, server)) {
            channel.sendMessage(user.getMentionTag(), embedUtil.parseString(String.join(" ", args), user, server));
        } else if (channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC)) {
            channel.sendMessage(user.getMentionTag(), embedUtil.parseString(String.join(" ", args), user, server));
        }
    }

}
