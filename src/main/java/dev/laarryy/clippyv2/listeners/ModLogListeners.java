package dev.laarryy.clippyv2.listeners;

import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.auditlog.AuditLog;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.auditlog.AuditLogEntry;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.event.server.role.UserRoleRemoveEvent;
import org.javacord.api.event.user.UserChangeNameEvent;
import org.javacord.api.event.user.UserChangeNicknameEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.MessageDeleteListener;
import org.javacord.api.listener.message.MessageEditListener;
import org.javacord.api.listener.server.member.ServerMemberBanListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;
import org.javacord.api.listener.server.role.UserRoleAddListener;
import org.javacord.api.listener.server.role.UserRoleRemoveListener;
import org.javacord.api.listener.user.UserChangeNameListener;
import org.javacord.api.listener.user.UserChangeNicknameListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


import static java.time.temporal.ChronoUnit.SECONDS;

public class ModLogListeners implements MessageEditListener, MessageDeleteListener, ServerMemberBanListener, ServerMemberJoinListener, ServerMemberLeaveListener, UserChangeNicknameListener, UserChangeNameListener, UserRoleAddListener, UserRoleRemoveListener {

    private final DiscordApi api;
    private final Optional<TextChannel> modChannel;

    public ModLogListeners(DiscordApi api) {
        modChannel = api.getTextChannelById(Constants.CHANNEL_LOGS);
        this.api = api;
    }

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


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

        embed.setAuthor("Message Deleted", "", "https://luckperms.net/logo.png");
        embed.setColor(new Color(0xCD6A23));

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

        embed.setAuthor("Message Edited", "", "https://luckperms.net/logo.png");
        embed.setColor(new Color(0x135E9C));

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
        String banReason = "Being bad";
        AuditLog log;
        Future<AuditLog> future = ev.getServer().getAuditLog(1, AuditLogActionType.MEMBER_BAN_ADD);

        try {
            log = future.get();
            for (AuditLogEntry entry : log.getEntries()) {
                if (entry.getTarget().get().getId() == ev.getUser().getId() && entry.getReason().isPresent()) {
                    bannedBy = entry.getUser().get().getNicknameMentionTag();
                    banReason = entry.getReason().get();
                    break;
                } else if (entry.getTarget().get().getId() == ev.getUser().getId()) {
                    bannedBy = entry.getUser().get().getNicknameMentionTag();
                    banReason = "No reason provided!";
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Member Banned", "", "https://luckperms.net/logo.png");
        embed.setColor(new Color(0xA70C0C));
        embed.addInlineField("Banned Member:", ev.getUser().getMentionTag());
        embed.addInlineField("Banned By: ", bannedBy);
        embed.addField("Reason", banReason);
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
            }
        }
        // Log it
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("New Member", "", "https://luckperms.net/logo.png");
        embed.setTitle(ev.getUser().getName());
        embed.setColor(new Color(0x13C108));
        embed.setThumbnail(ev.getUser().getAvatar());
        embed.addField("Account Created", Date.from(ev.getUser().getCreationTimestamp()).toString());
        embed.setFooter(ev.getUser().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent ev) {
        handleAuditLog(Instant.now(), ev.getUser(), ev.getServer().getAuditLog(5, AuditLogActionType.MEMBER_KICK));
    }

    private void handleAuditLog(Instant eventtime, User leftUser, CompletableFuture<AuditLog> auditLogFuture) {
        auditLogFuture.thenAcceptAsync(auditLog -> {
            List<AuditLogEntry> entries = auditLog.getEntries();
            for (int i = 0, i2 = entries.size(); i < i2; i++) {
                AuditLogEntry entry = entries.get(i);
                if (entry.getCreationTimestamp().plus(5, SECONDS).isBefore(eventtime)) {
                    EmbedBuilder leaveembed = new EmbedBuilder();
                    leaveembed.setAuthor("Member Left", null, "https://luckperms.net/logo.png");
                    leaveembed.addInlineField("Farewell,", leftUser.getMentionTag());
                    leaveembed.setColor(new Color(0xEF8805));
                    leaveembed.setThumbnail(leftUser.getAvatar());
                    leaveembed.setFooter(leftUser.getIdAsString());
                    leaveembed.setTimestamp(Instant.now());
                    modChannel.get()
                            .sendMessage(leaveembed)
                            .exceptionally(ExceptionLogger.get());
                    break;
                }

                User kickedUser = entry.getTarget().orElseThrow(AssertionError::new).asUser().join();
                if (!kickedUser.equals(leftUser)) {
                    if (i == i2 - 1) {
                        handleAuditLog(eventtime, leftUser, entry.getAuditLogBefore(5, AuditLogActionType.MEMBER_KICK));
                    }
                    continue;
                }

                User kickingUser = entry.getUser().join();
                String kickReason = entry.getReason().orElse("No reason provided.");
                EmbedBuilder kickembed = new EmbedBuilder();
                kickembed.setAuthor("Member Kicked", null, "https://luckperms.net/logo.png");
                kickembed.setColor(new Color(0xB44208));
                kickembed.addInlineField("Kicked Member:", leftUser.getMentionTag());
                kickembed.addInlineField("Kicked By: ", kickingUser.getMentionTag());
                kickembed.addField("Reason", kickReason);
                kickembed.setFooter(leftUser.getIdAsString());
                kickembed.setTimestamp(Instant.now());
                modChannel.get()
                        .sendMessage(kickembed)
                        .exceptionally(ExceptionLogger.get());
                break;
            }
        }, leftUser.getApi().getThreadPool().getExecutorService())
                .exceptionally(ExceptionLogger.get());
    }



    @Override
    public void onUserChangeName(UserChangeNameEvent ev) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Name Changed");
        embed.setColor(new Color(0xFFCDD206));
        embed.setThumbnail("https://luckperms.net/logo.png");

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

        embed.setAuthor("Nickname Changed", "", "https://luckperms.net/logo.png");
        embed.setColor(new Color(0xC46F06));
        embed.setThumbnail("https://luckperms.net/logo.png");

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

    @Override
    public void onUserRoleAdd(UserRoleAddEvent ev) {
        EmbedBuilder embed = new EmbedBuilder();
        Server server = ev.getServer();
        embed.setAuthor("Role Add", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", "https://luckperms.net/logo.png");
        embed.setTimestampToNow();
        embed.addField("Role Added:", ev.getRole().getMentionTag());
        embed.addField("To:", ev.getUser().getMentionTag());
        embed.setFooter("ID: " + ev.getUser().getIdAsString());
        embed.setColor(new Color(0x1084A7));

        modChannel.get().sendMessage(embed);
    }

    @Override
    public void onUserRoleRemove(UserRoleRemoveEvent ev) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Role Removed", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", "https://luckperms.net/logo.png");
        embed.setTimestampToNow();
        embed.addField("Role Removed:", ev.getRole().getMentionTag());
        embed.addField("From:", ev.getUser().getMentionTag());
        embed.setFooter("ID: " + ev.getUser().getIdAsString());
        embed.setColor(new Color(0xD04E0F));

        modChannel.get().sendMessage(embed);
    }
}