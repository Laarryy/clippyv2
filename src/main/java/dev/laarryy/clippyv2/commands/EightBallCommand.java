package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class EightBallCommand implements CommandExecutor {

    List<String> responses = new ArrayList<>();
    String img = "https://i.imgur.com/nBRPBMf.gif";

    public EightBallCommand() {
        try {
            File file = new File("data/8ball_responses.txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                responses.add(sc.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"!8ball"}, usage = "!8ball", description = "Asks the all knowing 8ball")
    public void onCommand(DiscordApi api, TextChannel channel, User user, String[] args) {
        if (args.length == 0 || !channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC)) {
            channel.sendMessage(user.getMentionTag() + " The 8ball can only be consulted in #off-topic and `!8ball <question>` must be used.");
            return;
        }
        channel.sendMessage(new EmbedBuilder().setTitle("Clippy shakes the magic 8ball..").setImage(img)).thenAcceptAsync(message -> {
            String[] answer = responses.get(ThreadLocalRandom.current().nextInt(responses.size() -1)).split("\\|");
            EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode(answer[0])).setTitle(answer[1]);
            message.getApi().getThreadPool().getScheduler().schedule(() -> message.edit(user.getMentionTag(), builder),4, TimeUnit.SECONDS);
        });

    }

}
