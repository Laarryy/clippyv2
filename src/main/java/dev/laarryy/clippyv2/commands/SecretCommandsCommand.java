package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import dev.laarryy.clippyv2.util.PagedEmbed;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class SecretCommandsCommand implements CommandExecutor {

    CommandHandler handler;

    public SecretCommandsCommand(CommandHandler commandHandler) {
        this.handler = commandHandler;
    }

    @Command(aliases = {"!secretcommands", ".secretcommands"}, usage = "!secretcommands", description = "shows all commands")
    public void onCommand(User user, TextChannel channel, Server server, Message message) {
        if (RoleUtil.isStaff(user, server)) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Commands").setColor(Color.GREEN);
            PagedEmbed pagedEmbed = new PagedEmbed(channel, embed);
            for (CommandHandler.SimpleCommand command : handler.getCommands()) {
                pagedEmbed.addField(command.getCommandAnnotation().usage(), "```" + command.getCommandAnnotation().description() + "```");
            }
            pagedEmbed.build().join();
        } else { message.addReaction("\uD83D\uDEAB"); }
    }
}
