package org.laarryy.clippyv2.listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.auditlog.AuditLog;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.auditlog.AuditLogEntry;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.event.user.UserChangeNameEvent;
import org.javacord.api.event.user.UserChangeNicknameEvent;
import org.javacord.api.listener.message.MessageDeleteListener;
import org.javacord.api.listener.message.MessageEditListener;
import org.javacord.api.listener.server.member.ServerMemberBanListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;
import org.javacord.api.listener.user.UserChangeNameListener;
import org.javacord.api.listener.user.UserChangeNicknameListener;
import org.laarryy.clippyv2.Constants;

import java.awt.*;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Future;

public class ModLogListeners implements MessageEditListener, MessageDeleteListener, ServerMemberBanListener, ServerMemberJoinListener, ServerMemberLeaveListener, UserChangeNicknameListener, UserChangeNameListener {

    private DiscordApi api;
    private Optional<TextChannel> modChannel;
    private Object Role;

    public ModLogListeners(DiscordApi api) {
        modChannel = api.getTextChannelById(Constants.CHANNEL_LOGS);
        this.api = api;
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent ev) {
        Message message;
        String deletedBy = "Self";
        AuditLog log;
        try {
            message = ev.getMessage().orElseGet(null);
        } catch (Exception e) { //Message is not cached
            return;
        }

        Future<AuditLog> future = ev.getServer().get().getAuditLog(10, AuditLogActionType.MESSAGE_DELETE);

        try {
            log = future.get();
            for (AuditLogEntry entry : log.getEntries()) {
                if (entry.getTarget().get().getId() == ev.getMessage().get().getAuthor().getId() && entry.getCreationTimestamp().isAfter(Instant.now().minus(Duration.ofMinutes(1)))) {
                    deletedBy = entry.getUser().get().getNicknameMentionTag();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Deletion");
        embed.setColor(Color.RED);
        embed.setThumbnail("https://i.imgur.com/p9bhOZh.png");

        embed.addInlineField("Author", message.getAuthor().asUser().get().getMentionTag());
        embed.addInlineField("Channel", String.format("<#%s>", ev.getChannel().getId()));
        embed.addInlineField("Deleted by", deletedBy);

        embed.addField("Message", "```" + stripGrave(ev.getMessage().get().getContent()) + "```");

        embed.setFooter(ev.getMessage().get().getUserAuthor().get().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onMessageEdit(MessageEditEvent ev) {
        if (!ev.getMessageAuthor().isPresent() || ev.getMessageAuthor().get().isYourself()) {
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Message Edited");
        embed.setColor(Color.YELLOW);
        embed.setThumbnail("https://i.imgur.com/ED8Gek4.png");

        embed.addInlineField("Author", ev.getMessage().get().getAuthor().asUser().get().getMentionTag());
        embed.addInlineField("Channel", String.format("<#%s>", ev.getChannel().getId()));

        embed.addField("Was", "```" + stripGrave(ev.getOldContent().get()) + "```");
        embed.addField("Now", "```" + stripGrave(ev.getNewContent()) + "```");

        embed.setFooter(ev.getMessage().get().getUserAuthor().get().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);

    }

    @Override
    public void onServerMemberBan(ServerMemberBanEvent ev) {
        String bannedBy = "Unknown";
        String reason = "Because";
        AuditLog auditLog;

        Future<AuditLog> future = ev.getServer().getAuditLog(10, AuditLogActionType.MEMBER_BAN_ADD);

        try {
            auditLog = future.get();
            for (AuditLogEntry entry : auditLog.getEntries()) {
                if (entry.getTarget().get().getId() == ev.getUser().getId()) {
                    bannedBy = entry.getUser().get().getNicknameMentionTag();
                    reason = entry.getReason().get();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(ev.getUser());
        embed.setColor(Color.PINK);
        embed.setThumbnail("https://i.imgur.com/IncrMxo.jpg");

        embed.addInlineField("Banned By: ", bannedBy);
        embed.addInlineField("ID", ev.getUser().getIdAsString());
        embed.addField("Reason", reason);

        embed.setFooter(ev.getUser().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent ev) {
        Server server = ev.getServer();
        // Role on join
        Optional<org.javacord.api.entity.permission.Role> member = ev.getServer().getRoleById(Constants.ROLE_MEMBER);
        {
        if (ev.getUser().getRoles(server).get(0).isEveryoneRole()) {
            ev.getUser().addRole((member.get()));
        };

    }
    // Log it
    EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(ev.getUser());
        embed.setTitle("Joined the server");
        embed.setColor(Color.GREEN);
        embed.setThumbnail(ev.getUser().getAvatar());
        embed.addField("Created",Date.from(ev.getUser().

    getCreationTimestamp()).

    toString());

        embed.setFooter(ev.getUser().

    getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().

    sendMessage(embed);
}


    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent ev) {
        String kickedBy = "Unknown";
        String kickReason = "Because";
        AuditLog log;

        Future<AuditLog> future = ev.getServer().getAuditLog(10, AuditLogActionType.MEMBER_KICK);

        try {
            log = future.get();
            for (AuditLogEntry entry : log.getEntries()) {
                if (entry.getTarget().get().getId() == ev.getUser().getId()) {
                    kickedBy = entry.getUser().get().getNicknameMentionTag();
                    kickReason = entry.getReason().get();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(ev.getUser());

        if (kickedBy.equalsIgnoreCase("unknown")) {
            embed.setTitle("Left the server");
            embed.setColor(Color.ORANGE);
        } else {
            embed.setColor(Color.PINK);
            embed.setThumbnail("https://i.imgur.com/IncrMxo.jpg");

            embed.addInlineField("Kicked By: ", kickedBy);
            embed.addField("Reason", kickReason);
        }

        embed.setFooter(ev.getUser().getIdAsString());
        embed.setTimestamp(Instant.now());
        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onUserChangeName(UserChangeNameEvent ev) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Name Changed");
        embed.setColor(Color.YELLOW);
        embed.setThumbnail("https://i.imgur.com/IncrMxo.jpg");

        embed.addInlineField("Old", ev.getOldName());
        embed.addInlineField("New", ev.getNewName());

        embed.setFooter(ev.getUser().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onUserChangeNickname(UserChangeNicknameEvent ev) {
        EmbedBuilder embed = new EmbedBuilder();
        Optional<String> oldnick = ev.getOldNickname();
        Optional<String> newnick = ev.getNewNickname();
        String name = ev.getUser().getName();

        embed.setAuthor("Nickname Changed");
        embed.setColor(Color.YELLOW);
        embed.setThumbnail("https://i.imgur.com/IncrMxo.jpg");

        {
            if (newnick.isPresent()) {
                embed.addInlineField("New", newnick.get());
            } else
                embed.addInlineField("New", name);
            if (oldnick.isPresent()) {
                embed.addInlineField("Old", oldnick.get());
            } else
                embed.addInlineField("Old", "**Nothing**");
        }
        embed.addField("ID", ev.getUser().getIdAsString());

        embed.setFooter(ev.getUser().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    public String stripGrave(String string) {
        return string.replace("`", "");
    }

}