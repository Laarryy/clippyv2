package org.laarryy.clippyv2;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.laarryy.clippyv2.commands.*;
import org.laarryy.clippyv2.commands.moderation.BanCommand;
import org.laarryy.clippyv2.commands.moderation.KickCommand;
import org.laarryy.clippyv2.commands.moderation.PruneCommand;
import org.laarryy.clippyv2.listeners.AutoModListeners;
import org.laarryy.clippyv2.listeners.ModLogListeners;
import org.laarryy.clippyv2.listeners.PrivateListener;
import org.laarryy.clippyv2.listeners.StarboardListener;
import org.laarryy.clippyv2.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    // The logger for this class.
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) { System.out.println(args[i]); }
        if (args.length != 1) {
            logger.error("Invalid amount of arguments provided!");
            return;
        }

        DiscordApi api = new DiscordApiBuilder().setToken(args[0]).login().join();
        logger.info("Logged in to Discord account {}", api.getYourself().getName());

        // Create command handler
        CommandHandler commandHandler = new JavacordHandler(api);

        // Give bot owner all permissions.
        commandHandler.addPermission(String.valueOf(api.getOwnerId()), "*");

        // Register commands
        // To enable a command, add or uncomment it here. To disable, comment it out!
        commandHandler.registerCommand(new BStatsCommand());
        commandHandler.registerCommand(new TagCommand(api));
        commandHandler.registerCommand(new GithubCommand());
        commandHandler.registerCommand(new BanCommand());
        commandHandler.registerCommand(new KickCommand());
        commandHandler.registerCommand(new PruneCommand());
        commandHandler.registerCommand(new MojangCommand());
        commandHandler.registerCommand(new RoleCheckCommand());
        commandHandler.registerCommand(new PresenceCommand());
        commandHandler.registerCommand(new NicknameCommand());
        commandHandler.registerCommand(new AvatarCommand());
        commandHandler.registerCommand(new SpigetCommand());
        commandHandler.registerCommand(new CommandsCommand(commandHandler));
        //commandHandler.registerCommand(new EssentialsXCommand());
        commandHandler.registerCommand(new RoleReactionCommand(api));
        commandHandler.registerCommand(new EmbedCommand());
        commandHandler.registerCommand(new SayCommand());
        commandHandler.registerCommand(new UserTagCommand(api));
        //commandHandler.registerCommand(new SpaceXCommand());
        //commandHandler.registerCommand(new XkcdCommand());
        commandHandler.registerCommand(new WolframAlphaCommand());
        //commandHandler.registerCommand(new EightBallCommand());
        //commandHandler.registerCommand(new FbiCommand());
        commandHandler.registerCommand(new CakeCommand());

        // Register listeners
        api.addListener(new ModLogListeners(api));
        api.addListener(new AutoModListeners(api, commandHandler));
        api.addListener(new PrivateListener(api));
        //api.addReactionAddListener(new StarboardListener(api));

    }

}