package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class SayCommand implements CommandExecutor {

    @Command(aliases = {"!say", ".say"}, usage = "!say <channel> <words>", description = "Say stuff")
    public void onSay(TextChannel channel, String[] args, User user, Message message, Server server) {
        if (RoleUtil.isStaff(user, server)) {
            if (message.getMentionedChannels().size() >= 1) {
                message.getMentionedChannels().get(0).sendMessage(String.join(" ", args).substring(args[0].length()));
            } else {
                if (message.getMentionedUsers().size() >= 1) {
                    message.getMentionedUsers().get(0).sendMessage(String.join(" ", args).substring(args[0].length()));
                } else {
                    channel.sendMessage(String.join(" ", args));
                }
            }
        } else {
            message.addReaction("\uD83D\uDEAB");
        }
    }
}
