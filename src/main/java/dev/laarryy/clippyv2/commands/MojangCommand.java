package dev.laarryy.clippyv2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.BStatsUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class MojangCommand implements CommandExecutor {

    @Command(aliases = {"!mojang", "!mcstatus"}, usage = "!mojang", description = "Shows Mojang AuthServer Status")
    public void onCommand(DiscordApi api, TextChannel channel) {
        BStatsUtil bStatsUtil = new BStatsUtil(api);
        EmbedBuilder embed = new EmbedBuilder();
        try {

            JsonNode status = bStatsUtil.makeRequest("http://status.mojang.com/check");
            embed.setAuthor("Mojang Status");
            embed.setThumbnail("https://i.imgur.com/uPuh3cT.png");

            embed.addInlineField("Mojang | AuthServer", parseStatus(status.get(3).get("authserver.mojang.com").asText()));
            embed.setTimestampToNow();

        } catch (Exception e) {
            embed.addField("Error", "```"+e.toString() + " @ " +e.getStackTrace()[0]+"```");
        }

        channel.sendMessage(embed);
    }

    public String parseStatus(String status) {
        if (status.equalsIgnoreCase("green")) {
            return "\u2705";
        }
        return "\u274C";
    }
}
