package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.Date;

public class UserInfoCommand implements CommandExecutor {
    @Command(aliases = {"!userinfo", ".userinfo", "!uinfo", ".uinfo"}, usage = "!uinfo <name>", description = "Gets info of a user")
    public void onCommand(String[] args, User cmdSender, Server server, Message message, TextChannel channel) {
        if (RoleUtil.isStaff(cmdSender, server)) {
            if (args.length >= 1 && message.getMentionedUsers().size() >= 1) {
                for (User user : message.getMentionedUsers()) {
                    if (user.getJoinedAtTimestamp(server).isPresent()) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(new Color(0x30D5B2));
                        embed.setAuthor(user.getName(), null, user.getAvatar());
                        embed.addInlineField(":person_bowing: User", user.getDiscriminatedName());
                        if (user.getStatus().getStatusString().equalsIgnoreCase("online")) {
                            embed.addInlineField(":grey_question: Status", ":green_circle: Online");
                        } else
                        if (user.getStatus().getStatusString().equalsIgnoreCase("idle")) {
                            embed.addInlineField(":grey_question: Status", ":crescent_moon: Idle");
                        } else
                        if (user.getStatus().getStatusString().equalsIgnoreCase("dnd")) {
                            embed.addInlineField(":grey_question: Status", ":no_entry: Do Not Disturb");
                        } else
                        if (cmdSender.getStatus().getStatusString().equalsIgnoreCase("offline")){
                            embed.addInlineField(":grey_question: Status", ":black_circle: Offline");
                        } else {
                            embed.addInlineField(":grey_question: Status:", cmdSender.getStatus().getStatusString());
                        }
                        embed.setThumbnail(user.getAvatar());
                        embed.addField(":calendar_spiral: Account Created:", Date.from(user.getCreationTimestamp()).toString());
                        embed.addInlineField(":calendar: Joined server:", Date.from(user.getJoinedAtTimestamp(server).get()).toString());
                        embed.setFooter("ID: " + user.getIdAsString());
                        channel.sendMessage(embed);
                    } else {
                        message.addReaction("\uD83D\uDC4E");
                    }
                }
            } else {
                if (cmdSender.getJoinedAtTimestamp(server).isPresent()) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(0x30D5B2));
                    embed.setAuthor(cmdSender.getName(), null, cmdSender.getAvatar());
                    embed.addInlineField(":person_bowing: User", cmdSender.getDiscriminatedName());
                        if (cmdSender.getStatus().getStatusString().equalsIgnoreCase("online")) {
                            embed.addInlineField(":grey_question: Status", ":green_circle: Online");
                        } else
                        if (cmdSender.getStatus().getStatusString().equalsIgnoreCase("idle")) {
                            embed.addInlineField(":grey_question: Status", ":crescent_moon: Idle");
                        } else
                        if (cmdSender.getStatus().getStatusString().equalsIgnoreCase("dnd")) {
                            embed.addInlineField(":grey_question: Status", ":no_entry: Do Not Disturb");
                        } else
                        if (cmdSender.getStatus().getStatusString().equalsIgnoreCase("offline")){
                            embed.addInlineField(":grey_question: Status", ":black_circle: Offline");
                        } else {
                            embed.addInlineField(":grey_question: Status:", cmdSender.getStatus().getStatusString());
                        }
                    embed.setThumbnail(cmdSender.getAvatar());
                    embed.addField(":calendar_spiral: Account Created:", Date.from(cmdSender.getCreationTimestamp()).toString());
                    embed.addInlineField(":calendar: Joined server:", Date.from(cmdSender.getJoinedAtTimestamp(server).get()).toString());
                    embed.setFooter("ID: " + cmdSender.getIdAsString());
                    channel.sendMessage(embed);
                } else {
                    message.addReaction("\uD83D\uDEAB");
                }
            }
        } else {
            message.addReaction("\uD83D\uDEAB");
        }
    }
}