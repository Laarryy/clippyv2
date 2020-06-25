package org.laarryy.clippyv2.commands.moderation;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.laarryy.clippyv2.Constants;
import org.laarryy.clippyv2.util.RoleUtil;

import java.awt.*;
import java.util.List;

public class MuteCommand implements CommandExecutor {

    @Command(aliases = {"!mute"}, usage = "!mute <username> <reason>", description = "Mutes a chosen user.")
    public void onCommand(TextChannel channel, String[] args, Message message, MessageAuthor messageAuthor, Server server) {
        if (!(messageAuthor.canMuteMembersOnServer() && messageAuthor.isYourself())) {
            return;
        }
        if (message.getMentionedUsers().size() == 0) {
            channel.sendMessage("Mute who?");
            return;
        }
        if (RoleUtil.hasStaffMention(message)) {
            channel.sendMessage("Can't mute staff!");
            return;
        }
        if (args.length >= 2) {
            User user = message.getMentionedUsers().get(0);
            String reason = String.join(" ", args).substring(args[0].length());

            server.muteUser(user, reason);

            channel.sendMessage("Successfully muted " + user.getMentionTag() + " for: " + reason);
        }
        else
            channel.sendMessage("No.");
    }

    @Command(aliases = {"!unmute"}, usage = "!unmute <username>", description =  "Unmutes a user.")
    public void onCommand(DiscordApi api, String[] args, TextChannel channel, Message message, MessageAuthor messageAuthor, Server server, User user) {
        if (!messageAuthor.canKickUsersFromServer()) {
            return;
        }
        if (message.getMentionedUsers().size() == 0) {
            channel.sendMessage("Mute who?");
            return;
        }
        if (args.length <= 2) {
            String reason = String.join(" ", args).substring(args[0].length());

            message.getServer().get().unmuteUser(message.getMentionedUsers().get(0));

            channel.sendMessage("Unmuted " + message.getMentionedUsers().get(0).getMentionTag());
        } else {
            channel.sendMessage("Please specify a user!");
        }
    }
}

