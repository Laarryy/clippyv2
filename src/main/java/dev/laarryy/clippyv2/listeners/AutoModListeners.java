package dev.laarryy.clippyv2.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoModListeners implements MessageCreateListener, CommandExecutor {

    DiscordApi api;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private ModerationData data;
    private final Optional<TextChannel> modChannel;
    private final Map<Long, Instant> active = new HashMap<>();
    private final Map<Long, MessageTracker> trackers = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<String> protectedRoles = new ArrayList<String>() {
        {
            add(Constants.ROLE_STAFF);
            add(Constants.ROLE_HELPFUL);
        }
    };

    String[] donts = {
            "Please, no longer",
            "I beg of you, cease.",
            "Could you not please?",
            "Read the #rules",
            "This is not allowed."

    };

    String[] nopes = {
            "Do not ping staff/helpful people.",
    };

    public AutoModListeners(DiscordApi api, CommandHandler commandHandler) {
        this.api = api;
        commandHandler.registerCommand(this);
        modChannel = api.getTextChannelById(Constants.CHANNEL_LOGS);
        data = new ModerationData();
        try {
            JsonNode jsonNode = mapper.readTree(new File("./moddata.json")).get("data");
            data = mapper.readValue(jsonNode.toString(), ModerationData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"!pingok", ".pingok"}, usage = "!pingok", description = "Pings on the channel are ok")
    public void onOk(DiscordApi api, String[] args, TextChannel channel, User user, Message message, Server server) {
        if (!(server.canKickUsers(user) || hasProtectedRole(user.getRoles(server)))) {
            return;
        }
        try {
            if (data.exempts.get(user.getId()).contains(channel.getIdAsString())) {
                data.exempts.get(user.getId()).remove(channel.getIdAsString());
                message.addReaction("\ud83d\ude08");
            } else {
                data.exempts.computeIfAbsent(user.getId(), daList -> new TreeSet<>()).add(channel.getIdAsString());
                message.addReaction("\ud83d\ude01");
            }
        } catch (Exception e) {
            data.exempts.computeIfAbsent(user.getId(), daList -> new TreeSet<>()).add(channel.getIdAsString());
            message.addReaction("\ud83d\ude01");
        }
        saveModData();
    }

    @Command(aliases = {"!fileblacklist", ".fileblacklist"}, usage = "!fileblacklist <Extension>", description = "Adds a file blacklist")
    public void addBlackList(DiscordApi api, String[] args, TextChannel channel, MessageAuthor author, Message message) {
        if (author.canBanUsersFromServer() && args.length >= 1) {
            if (args[0].startsWith(".")) {
                if (data.blacklistedFiles.contains(args[0])) {
                    data.blacklistedFiles.remove(args[0].toLowerCase());
                    message.addReaction("\uD83D\uDC4E");
                } else {
                    data.blacklistedFiles.add(args[0].toLowerCase());
                    message.addReaction("\uD83D\uDC4D");
                }
                saveModData();
            } else {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Invalid file extension"));
            }
        }
    }

    @Command(aliases = {"!addcensor", ".addcensor"}, usage = "!addcensor <Word/Regex>", description = "Adds the words to the censor list")
    public void addCensor(DiscordApi api, String[] args, TextChannel channel, MessageAuthor author, Message message) {
        if (author.canBanUsersFromServer() && args.length >= 1) {
            if (data.censoredWords.contains(args[0])) {
                data.censoredWords.remove(args[0].toLowerCase());
                message.addReaction("\uD83D\uDC4E");
            } else {
                data.censoredWords.add(args[0].toLowerCase());
                message.addReaction("\uD83D\uDC4D");
            }
            saveModData();
        }
    }


    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        if (ev.getMessage().getAuthor().isYourself() || ev.getMessage().getAuthor().canKickUsersFromServer()) {
            active.put(ev.getMessage().getAuthor().getId(), Instant.now());
            return;
        }

        parsePings(ev.getMessage());

        if (!ev.getMessage().getEmbeds().isEmpty()) {
            for (Embed embed : ev.getMessage().getEmbeds()) {
                String title = embed.getTitle().orElse("");
                System.out.println(title);
                for (String pattern : data.censoredWords) {
                    if (title.toLowerCase().contains(pattern)) {
                        ev.getMessage().delete("Pattern trigger: " + pattern);
                        logCensorMessage(ev.getMessage().getUserAuthor(), pattern, ev.getChannel().getIdAsString(), ev.getMessage());
                        return;
                    }
                }
            }
        }
        // deletes blacklisted filetypes. Optional but not preferred due to
        // the wide variety of legitimate reasons to attach files to messages
        /*for (MessageAttachment messageAttachment : ev.getMessage().getAttachments()) {
            String fileName = messageAttachment.getFileName();
            if (data.blacklistedFiles.contains(fileName.substring(fileName.lastIndexOf('.')))) {
                ev.getMessage().delete("Blacklisted File: " + fileName);
                logFileMessage(ev.getMessage().getUserAuthor(), fileName, ev.getChannel().getIdAsString());
                return;
            }
        }*/

        String message = ev.getMessage().getContent();
        for (String pattern : data.censoredWords) {
            Matcher mat = Pattern.compile(pattern).matcher(message.toLowerCase());
            if (mat.find()) {
                ev.getMessage().delete("Pattern trigger: " + pattern);
                logCensorMessage(ev.getMessage().getUserAuthor(), mat.group(), ev.getChannel().getIdAsString(), ev.getMessage());
                return;
            }
        }
    }

    public void parsePings(Message message) {
        if (message.getMentionedUsers().size() >= 1) {
            User perp = message.getUserAuthor().get();
            Server server = message.getServer().get();
            MessageTracker tracker = getTrackedUser(perp.getId());
            boolean warn = false;
            for (User user : message.getMentionedUsers()) {
                if (server.canKickUsers(user) || hasProtectedRole(user.getRoles(server))) {
                    if (data.exempts.containsKey(user.getId()) && data.exempts.get(user.getId()).contains(message.getChannel().getIdAsString())) {
                        continue;
                    }
                    if (user.isBot()) {
                        continue;
                    }
                    warn = true;
                }
            }
            if (warn) {
                tracker.updatePings();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setDescription("Please do not ping staff!");
                embed.setFooter(String.format("%d", tracker.getCount()) + " | " + donts[ThreadLocalRandom.current().nextInt(donts.length)]);
                if (tracker.getCount() > 3) { //4th ping will kick the user.
                    message.getServer().get().kickUser(perp,"Mass ping");
                    message.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(String.format("%s has been kicked for not listening.", perp.getMentionTag())));
                    return;
                }
                message.getChannel().sendMessage(perp.getMentionTag(), embed);
                embed.setImage("https://i.imgur.com/j5P7kdV.png");
                // DMs them the above warning:
                // perp.sendMessage(embed);
            }
            if (warn) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor((new Color(174, 38, 245)));
                embed.setImage(perp.getAvatar());
                embed.setAuthor("Staff Pinged!");
                embed.setDescription("What a madlad that " + perp.getMentionTag() + " is!");
                embed.setFooter(String.format(" Warning %d", tracker.getCount()));
                modChannel.get().sendMessage(embed);
            }
        }
    }

    public void logCensorMessage(Optional<User> user, String pattern, String chanId, Message message) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Message Censored");
        embed.setColor(Color.CYAN);
        embed.setThumbnail("https://i.imgur.com/J015ZK5.png");

        embed.addInlineField("Author", user.get().getMentionTag());
        embed.addInlineField("Channel", String.format("<#%s>", chanId));

        embed.addField("Pattern", String.format("```%s```", pattern));
        embed.addInlineField("Contents",message.getContent());

        embed.setFooter(user.get().getIdAsString());
        embed.setTimestamp(Instant.now());
        modChannel.get().sendMessage(embed);
    }

    public void logFileMessage(Optional<User> user, String fileName, String chanId) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("FILE");
        embed.setColor(Color.CYAN);
        embed.setThumbnail("https://i.imgur.com/bYGnGCp.png");

        embed.addInlineField("Author", user.get().getMentionTag());
        embed.addInlineField("Channel", String.format("<#%s>", chanId));

        embed.addField("File", String.format("```%s```", fileName));

        embed.setFooter(user.get().getIdAsString());
        embed.setTimestamp(Instant.now());

        modChannel.get().sendMessage(embed);
    }

    private void saveModData() {
        try {
            mapper.writerWithDefaultPrettyPrinter().withRootName("data").writeValue(new File("./moddata.json"), data);
        } catch (Throwable rock) {
            rock.printStackTrace();
        }
    }

    private MessageTracker getTrackedUser(long id) {
        if (!trackers.containsKey(id)) {
            trackers.put(id, new MessageTracker());
        }
        return trackers.get(id);
    }

    static class ModerationData {
        public Map<Long, Set<String>> exempts = new HashMap<>();
        public List<String> blacklistedFiles;
        public List<String> censoredWords;
    }

    private boolean hasProtectedRole(List<Role> roles) {
        for (Role role : roles) {
            if (protectedRoles.contains(role.getIdAsString())) {
                return true;
            }
        }
        return false;
    }

    class MessageTracker {

        private final AtomicInteger count;
        private String lastMessage;

        public int updatePings () {
            return count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }

        MessageTracker() {
            this.count = new AtomicInteger(0);
            api.getThreadPool().getScheduler().scheduleAtFixedRate(() -> {
                if (count.get() > 0) {
                    count.decrementAndGet();
                }
            }, 0,30, TimeUnit.MINUTES);
        }
    }
}