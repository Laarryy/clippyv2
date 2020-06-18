package org.laarryy.clippyv2.commands;

import ch.qos.logback.classic.Logger;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.laarryy.clippyv2.Constants;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;

public class FbiCommand implements CommandExecutor {


    private static final Logger LOGGER = null;

    @Command(aliases = {"!fbi", ".fbi", "!piracy", ".piracy", ".pirate", "!pirate"}, usage = "!fbi", description = "Special message for thieves and their friends")
    public void onCommand(String[] args, TextChannel channel, MessageAuthor messageAuthor, Server server, Message message) {

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("PIRACY = BAD").setUrl("https://admincraft.host/fbi");
        embed.setColor(Color.RED);
        embed.setThumbnail("https://i.imgur.com/gu514NL.png");

        embed.addInlineField("Offline Mode? ", "Running an offline server allows people who have stolen the game to play it. Not only that, but it opens the door for a vast number of security threats that are best avoided.");
        embed.addInlineField("Stolen Plugins?", "Stealing the work of developers who ask for payment in exchange for their plugins is just plain wrong. If you have done this, you are not entitled to any assistance whatsoever.");
        embed.addField("What to do?", "Set `online-mode` to true in the server.properties file, or the bungeecord config if you run a network.");

        embed.setFooter(new String("A Message for a Pirate"));
        embed.setTimestamp(Instant.now());

        channel.sendMessage(embed);

        if (args.length >= 1) {
            message.getMentionedUsers();
            Optional<org.javacord.api.entity.permission.Role> pirate = server.getRoleById(Constants.ROLE_PIRATE);
            if (message.getMentionedUsers().get(0).getIdAsString().equals(Constants.USER_ZML))
            return;
            {
                if (!pirate.isPresent()) {
                    LOGGER.error("Could not find pirate role in Server {0}", server);
                    return;
                }
                if (!message.getAuthor().canKickUsersFromServer())
                return;

                for (User user : message.getMentionedUsers()) {
                    if (user.getIdAsString().equals(Constants.USER_ZML))
                        continue;

                    user.addRole(pirate.get());
                }
            }
        }
    }
}