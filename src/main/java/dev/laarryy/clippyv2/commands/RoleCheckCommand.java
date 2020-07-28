package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.ChannelUtil;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.stream.Collectors;

public class RoleCheckCommand implements CommandExecutor {

    @Command(aliases = {"!rolecheck", ".rolecheck"}, usage = "!rolecheck <User>", description = "Checks users' role")
    public void onCommand(TextChannel channel, String[] args, Message message, Server server, User cmdUser) {
        if (args.length >= 1 && RoleUtil.isStaff(cmdUser, server) && ChannelUtil.isNonPublicChannel(channel)) {
            String string = "User Roles```";
            if (message.getMentionedUsers().size() >= 1) {
                for (User user : message.getMentionedUsers()) {
                    channel.sendMessage("User Roles for " + user.getName() + String.format("```%s```", user.getRoles(server).stream().map(Role::getName).collect(Collectors.joining(", "))));
                }
                return;
            }
            for (User user : server.getMembers()) {
                if (user.getName().equalsIgnoreCase(args[0])) {
                    channel.sendMessage("User Roles for " + user.getName() + String.format("```%s```", user.getRoles(server).stream().map(Role::getName).collect(Collectors.joining(", "))));
                    break;
                }
            }
        } else {
            message.addReaction("\uD83D\uDEAB");
        }
    }
}

