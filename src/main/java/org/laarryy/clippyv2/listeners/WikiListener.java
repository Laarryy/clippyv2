package org.laarryy.clippyv2.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.internal.EmbedBuilderDelegate;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiListener implements MessageCreateListener {
    private static final Pattern pattern = Pattern.compile("^[!.](\\w+)");
    List<WikiCommand> commands;

    public WikiListener(DiscordApi api) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            commands = Arrays.asList(mapper.readValue(new URL("https://raw.githubusercontent.com/LuckPerms/clippy/master/modules/commands/list.json"), WikiCommand[].class));
        } catch (IOException e) {
            e.printStackTrace();
            api.removeListener(this);
        }
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        //noinspection OptionalGetWithoutIsPresent
        if (event.getMessageAuthor().isWebhook() || event.getMessageAuthor().asUser().get().isBot()) return;
        Matcher matcher = pattern.matcher(event.getMessageContent());
        if (!matcher.matches()) return;
        String commandName = matcher.group(1);
        WikiCommand command = null;
        for (WikiCommand commandLoop : commands) {
            if (commandLoop.name.equals(commandName)){
                command = commandLoop;
                continue;
            }
            if (commandLoop.aliases != null) {
                if (Arrays.asList(commandLoop.aliases).contains(commandName)) {
                    command = commandLoop;
                    continue;
                }
            }
        }
        if (commandName.equalsIgnoreCase("help")) command = getHelpCommand();
        if (command == null) return;
        event.getChannel().sendMessage(command.asEmbed()).join();
    }

    private WikiCommand getHelpCommand() {
        WikiCommand command = new WikiCommand();
        command.setTitle("Available Commands");
        StringBuilder commands1 = new StringBuilder();
        StringBuilder commands2 = new StringBuilder();
        commands.forEach(wikiCommand -> {
            if (commands.indexOf(wikiCommand) <= commands.size()/2)
                commands1.append("`!").append(wikiCommand.getName()).append("`\n");
            else
                commands2.append("`!").append(wikiCommand.getName()).append("`\n");
        });
        command.setFields(new WikiCommand.Field[]{
                new WikiCommand.Field().setKey("\u200B").setValue(commands1.toString()).setInline(true),
                new WikiCommand.Field().setKey("\u200B").setValue(commands2.toString()).setInline(true)
        });
        return command;
    }

    static class WikiCommand {
        private String name;
        private String[] aliases;
        private String title;
        private String url;
        private String description;
        private Field[] fields;
        private boolean wiki;
        private String message;
        private boolean sendMessageAfterEmbed;

        public EmbedBuilder asEmbed() {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(wiki ? "\uD83D\uDD16 " + title : title);
            embed.setDescription(description);
            if (wiki) {
                embed.setUrl(url);
                embed.addField("Read More: ", url);
                embed.setFooter("Luckperms Wiki", "https://luckperms.net/logo.png");
            }
            //embed RGB 148,223,3
            embed.setColor(new Color(148, 223, 3));
            Arrays.asList(fields).forEach(field -> embed.addField(field.key, field.value, field.inline));
            return embed;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getAliases() {
            return aliases;
        }

        public void setAliases(String[] aliases) {
            this.aliases = aliases;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Field[] getFields() {
            return fields;
        }

        public void setFields(Field[] fields) {
            this.fields = fields;
        }

        public boolean getWiki() {
            return wiki;
        }

        public void setWiki(boolean wiki) {
            this.wiki = wiki;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSendMessageAfterEmbed() {
            return sendMessageAfterEmbed;
        }

        public void setSendMessageAfterEmbed(boolean sendMessageAfterEmbed) {
            this.sendMessageAfterEmbed = sendMessageAfterEmbed;
        }

        @Override
        public String toString() {
            return "WikiCommand{" +
                    "name='" + name + '\'' +
                    ", aliases=" + Arrays.toString(aliases) +
                    ", title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    ", description='" + description + '\'' +
                    ", fields=" + Arrays.toString(fields) +
                    ", wiki=" + wiki +
                    ", message='" + message + '\'' +
                    ", sendMessageAfterEmbed=" + sendMessageAfterEmbed +
                    '}';
        }

        static class Field {
            private String key;
            private String value;
            private boolean inline;

            public String getKey() {
                return key;
            }

            public Field setKey(String key) {
                this.key = key;
                return this;
            }

            public String getValue() {
                return value;
            }

            public Field setValue(String value) {
                this.value = value;
                return this;
            }

            public boolean isInline() {
                return inline;
            }

            public Field setInline(boolean inline) {
                this.inline = inline;
                return this;
            }
        }
    }
}
