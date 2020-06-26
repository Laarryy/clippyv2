package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class NicknameCommand implements CommandExecutor {

    @Command(aliases = {"!setnick", ".setnick"}, usage = "!setnick <name>", description = "Sets the nickname of the bot")
    public void onCommand(DiscordApi api, String[] args, User user, Server server) {
        if (RoleUtil.isStaff(user.getRoles(server))) {
            if (args.length == 1) {
                api.getYourself().updateNickname(server, args[0]);
            } else {
                api.getYourself().updateNickname(server, api.getYourself().getName());
            }
        }
    }
}
