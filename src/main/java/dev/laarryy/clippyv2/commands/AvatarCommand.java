package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;

public class AvatarCommand implements CommandExecutor {

    private final DiscordApi api;
    private final Optional<TextChannel> modChannel;

    public AvatarCommand(DiscordApi api) {
        modChannel = api.getTextChannelById(Constants.CHANNEL_LOGS);
        this.api = api;
    }



    @Command(aliases = {"!avatar", ".avatar"}, usage = "!avatar <User>", description = "Shows the users' avatar")
    public void onCommand(DiscordApi api, String[] args, TextChannel channel, Message message, Server server, MessageAuthor messageAuthor) {
        if (args.length >= 1 && messageAuthor.canBanUsersFromServer()) {
            if (message.getMentionedUsers().size() >= 1) {
                channel.sendMessage(new EmbedBuilder().setImage(message.getMentionedUsers().get(0).getAvatar()));
                return;
            }
            for (User user : server.getMembers()) {
                if (user.getName().equalsIgnoreCase(args[0])) {
                    channel.sendMessage(new EmbedBuilder().setImage(user.getAvatar()));
                    return;
                }
            }
        }
    }

    @Command(aliases = {"!setavatar"}, usage = "!setavatar <img>", description = "Sets the bot's avatar")
    public void onSetAvatar(DiscordApi api, String[] args, TextChannel channel, Message message, MessageAuthor messageAuthor) {
        if (args.length >= 1 && messageAuthor.canBanUsersFromServer()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Bot Avatar Changed");
            embed.setColor(Color.YELLOW);
            embed.setThumbnail("https://i.imgur.com/2Hbdxuz.png");
            embed.addInlineField("Bot Avatar Changed By", message.getAuthor().asUser().get().getMentionTag());
            embed.addField("Their ID", messageAuthor.getIdAsString());
            embed.setFooter("Done by "+messageAuthor.getName());
            embed.setTimestamp(Instant.now());
            modChannel.get().sendMessage(embed);
            try {
                URL url = new URL(args[0]); //pray to god its a URL
                message.delete();
                api.createAccountUpdater().setAvatar(url).update();
                channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle("Avatar set!").setImage(args[0]));
            } catch (Exception e) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Unable to set avatar").setDescription("`!setavatar <url>`"));
            }
        }
    }
}
