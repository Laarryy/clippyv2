package dev.laarryy.clippyv2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.util.ChannelUtil;
import dev.laarryy.clippyv2.util.EmbedUtil;
import dev.laarryy.clippyv2.util.KeywordsUtil;
import dev.laarryy.clippyv2.util.RoleUtil;
import org.apache.commons.lang.ArrayUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

public class UserTagCommand implements CommandExecutor, MessageCreateListener {

    private UserTagData data = new UserTagData();
    private final EmbedUtil embedUtil = new EmbedUtil();
    private final ObjectMapper mapper = new ObjectMapper();

    public UserTagCommand(DiscordApi api) {
        api.addListener(this);
        try {
            JsonNode jsonTags = mapper.readTree(new File("./user_tags.json")).get("data");
            data = mapper.readValue(jsonTags.toString(), UserTagData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"!utags"}, usage = "!utags [filter]", description = "List all currently enabled user tags.")
    public void onList(Message message, TextChannel channel, Server server, User user) {
        if (!RoleUtil.isStaff(user, server)) {
            message.addReaction("\uD83D\uDEAB");
            return;
        }
        String s = "";
        ArrayList<String> tagsList = new ArrayList<>(data.tagMap.keySet());
        Collections.sort(tagsList);
        for (String key : tagsList) {
            s += String.format("`%s` ", key);
        }
        channel.sendMessage(new EmbedBuilder().addField("Active User Tags", s).setColor(Color.GREEN).setFooter(String.format("%d users whitelisted", data.whitelist.size())));
    }


    @Command(aliases = {"!utagset"}, usage = "!utagset <name> [message]", description = "Set a user tag")
    public void onSet(Message message, TextChannel channel, String[] args, User user, Server server) {
        String key = args[0].toLowerCase();
        if (key.contains("\\n")) {
            channel.sendMessage("Invalid tag name!");
            return;
        }
        if (!(ChannelUtil.isNonPublicChannel(channel) || RoleUtil.isStaff(user, server))) {
            message.addReaction("\uD83D\uDEAB");
            return;
        }
        if (args.length >= 2 && data.tagMap.containsKey(key)) {
            if (!user.getIdAsString().equals(data.tagMap.get(key).owner)) {
                channel.sendMessage(new EmbedBuilder().setTitle("Tag already exists").setColor(Color.RED));
                return;
            }
        }
        String id = user.getIdAsString();
        if (args.length >= 2 && data.whitelist.contains(id)) {
            StringJoiner sb = new StringJoiner(" ");
            for(int i = 1; i < args.length; i++) {
                sb.add(args[i]);
            }
            UserTag newTag = new UserTag().setName(key).setContent(sb.toString()).setOwner(user.getIdAsString());
            data.tagMap.put(key, newTag);
            saveTags();
            channel.sendMessage(new EmbedBuilder().setTitle("User Tag set!").setColor(Color.GREEN));
        }
    }

    @Command(aliases = {"!utagunset", "!utagrelease"}, usage = "!utagunset <name> [message]", description = "Unset a user tag")
    public void onUnset(TextChannel channel, String[] args, User user, Server server) {
        if (args.length >= 1 && data.tagMap.containsKey(args[0].toLowerCase())) {
            if (data.tagMap.get(args[0].toLowerCase()).owner.equals(user.getIdAsString()) || RoleUtil.isStaff(user, server)) {
                data.tagMap.remove(args[0].toLowerCase());
                saveTags();
                channel.sendMessage(new EmbedBuilder().setTitle("Tag released!").setColor(Color.GREEN));
            } else {
                channel.sendMessage(new EmbedBuilder().setTitle("Tag is not yours!").setColor(Color.RED));
            }
        }
    }

    @Command(aliases = {"!utaginfo", "!uti"}, usage = "!utaginfo <name>", description = "Info of a user tag")
    public void onInfo(TextChannel channel, String[] args, User user, Server server) {
        if ((RoleUtil.isStaff(user, server) && ChannelUtil.isNonPublicChannel(channel))) {
            channel.sendMessage("Sorry, can't do that!");
            if (args.length >= 1 && data.tagMap.containsKey(args[0].toLowerCase())) {
                UserTag userTag = data.tagMap.get(args[0].toLowerCase());
                channel.sendMessage(user.getMentionTag(), new EmbedBuilder().setTitle(args[0] + " info").addInlineField("Created by", String.format("<@%s>", userTag.owner)).addInlineField("Modified on", Date.from(Instant.parse(userTag.modified)).toString()).addField("Content", String.format("```%s```", userTag.content)));
            }
        }
    }

    @Command(aliases = {"!utw"}, usage = "!utw", description = "Adds user to user tag whitelist.")
    public void onadd(User user, TextChannel channel, Server server, Message message) {
        if (RoleUtil.isStaff(user, server) && message.getMentionedUsers().size() >= 1) {
            for (User target : message.getMentionedUsers()) {
                String id = target.getIdAsString();
                if (data.whitelist.contains(id)) {
                    data.whitelist.remove(id);
                } else {
                    data.whitelist.add(id);
                }
            }
            saveTags();
            channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle("User(s) added/removed"));
        }
    }

    public void saveTags() {
        try {
            mapper.writerWithDefaultPrettyPrinter().withRootName("data").writeValue(new File("./user_tags.json"), data);
            //mapper.writerWithDefaultPrettyPrinter().withRootName("whitelist").writeValue(new File("./user_tags.json"), whitelist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        String message = ev.getMessage().getContent();
        if (message.startsWith("??") || message.startsWith("!!") || message.startsWith("..")) {
            String[] args = message.split(" ");
            String tag = args[0].substring(2).toLowerCase();
            args = (String[]) ArrayUtils.remove(args, 0);
            if (data.tagMap.containsKey(tag)) {
                String factoid = data.tagMap.get(tag).content;
                if (factoid.startsWith("<del>")) {
                    ev.getMessage().delete();
                    factoid = factoid.substring(5);
                }
                if (factoid.startsWith("<embed>") || factoid.startsWith("<json>")) {
                    ev.getChannel().sendMessage(embedUtil.parseString(factoid, ev.getMessage().getUserAuthor().get(), ev.getServer().get(), args));
                } else {
                    ev.getChannel().sendMessage(new KeywordsUtil(factoid, ev.getMessage().getUserAuthor().get(), ev.getServer().get(), args).replace());
                }
            }
        }
    }

    static class UserTagData {
        public Map<String, UserTag> tagMap = new HashMap<>();
        public Set<String> whitelist = new TreeSet();
    }

    static class UserTag {
        public String name;
        public String content;
        public String owner;
        public String modified;

        public UserTag setName(String name) {
            this.name = name;
            return this;
        }

        public UserTag setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public UserTag setContent(String content) {
            this.content = content;
            return this;
        }

        UserTag() {
            this.owner = "";
            this.name = "";
            this.content = "";
            this.modified = Instant.now().toString();
        }
    }

}
