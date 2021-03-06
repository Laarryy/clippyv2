package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.storage.RolePollStorage;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoleReactionCommand implements CommandExecutor, ReactionAddListener, ReactionRemoveListener {

    RolePollStorage storage = new RolePollStorage();
    Map<String, String> roleMap = new LinkedHashMap<>();

    public RoleReactionCommand(DiscordApi api) {
        api.addListener(this);
        roleMap.put("\ud83C\uDF2F", Constants.ROLE_LUCKPERMS_UPDATES);

    }

    @Command(aliases = {"!rolepoll", ".rolepoll"}, usage = "!rolepoll", description = "Polls users for update roles")
    public void onCommand(DiscordApi api, TextChannel channel, User user, Server server, Message cmd) {
        if (RoleUtil.isStaff(user, server)) {
            cmd.delete();
            try {
                Message msg = channel.sendMessage(createPoll()).get();
                roleMap.keySet().forEach(msg::addReaction);
                storage.set(msg.getIdAsString(), msg.getChannel().getIdAsString());
            } catch (Exception e) {}
        } else {
            cmd.addReaction("\uD83D\uDEAB");
        }
    }

    @Command(aliases = {"!rpupdate", ".rpupdate"}, usage = "!rpupdate", description = "Updates all roll polls.")
    public void onRPUpdate(DiscordApi api, TextChannel channel, User user, Server server, Message cmd) {
        if (RoleUtil.isStaff(user, server)) {
            cmd.delete();
            try {
                for (String key : storage.getMap().keySet()) {
                    api.getMessageById(key, api.getTextChannelById(storage.getChannel(key)).get()).thenAcceptAsync(msg -> {
                        msg.edit(createPoll());
                        roleMap.keySet().forEach(msg::addReaction);
                    });
                }
            } catch (Exception e) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Unable to update polls"));
            }
        }
    }

    private void broadcast(String payload, TextChannel channel, Role role) {
        try {
            role.createUpdater().setMentionableFlag(true).setAuditLogReason("Update command").update();
            channel.sendMessage(role.getMentionTag() + " " + payload);
            role.createUpdater().setMentionableFlag(false).update();
        } catch (Exception e) {
            e.printStackTrace();
            role.createUpdater().setMentionableFlag(false).update();
        }
    }

    private EmbedBuilder createPoll() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(Color.GREEN);
        embed.addField("Subscribe to plugin updates",
                "```\nClick the \ud83C\uDF2F to get pinged for important LuckPerms updates!```");
        return embed;
    }

    public void onReactionAdd(ReactionAddEvent event) {
        if (event.getUser().isYourself() || !storage.isPoll(event.getMessageId())) {
            return;
        }
        if (!event.getReaction().isPresent()) {
            event.requestReaction().thenAccept(reaction -> {
                if (reaction.isPresent() && storage.isPoll(event.getMessageId()) && reaction.get().containsYou()) {
                        updateRole(event.getUser(), roleMap.get(reaction.get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "add");
                } else {
                    event.removeReaction();
                }
            });
        } else if (storage.isPoll(event.getMessageId()) && event.getReaction().get().containsYou()) {
            updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "add");
        } else {
            event.removeReaction();
        }
    }

    public void onReactionRemove(ReactionRemoveEvent event) {
        if (event.getUser().isYourself()) {
            return;
        }
        if (storage.isPoll(event.getMessageId()) && event.getReaction().isPresent() && event.getReaction().get().containsYou()) {
            updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "remove");
        }
    }

    public void updateRole(User user, String role, Server server, String type) {
        Role target = server.getRoleById(role).get();
        if (user.getRoles(server).stream().anyMatch(role1 -> role1.getIdAsString().equalsIgnoreCase(role)) && type.equalsIgnoreCase("remove")) {
            user.removeRole(target, "Role Poll");
        } else {
            if (type.equalsIgnoreCase("add")) {
                user.addRole(target, "Role Poll");
            }
        }
    }

}
