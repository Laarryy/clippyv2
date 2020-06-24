package org.laarryy.clippyv2.listeners;


import okhttp3.Request;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.laarryy.clippyv2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.*;
import java.util.regex.Pattern;

public class ErrorListener implements MessageCreateListener {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final HashMap<Pattern, String> pastebins = new HashMap<>();

    public ErrorListener() {
        pastebins.put(Pattern.compile("https?://hastebin\\.com/(\\w+)(?:\\.\\w+)?"), "https://hastebin.com/raw/{code}");
        pastebins.put(Pattern.compile("https?://hasteb\\.in/(\\w+)(?:\\.\\w+)?"), "https://hasteb.in/raw/{code}");
        pastebins.put(Pattern.compile("https?://paste\\.helpch\\.at/(\\w+)(?:\\.\\w+)?"), "https://paste.helpch.at/raw/{code}");
        pastebins.put(Pattern.compile("https?://bytebin\\.lucko\\.me/(\\w+)"), "https://bytebin.lucko.me/{code}");
        pastebins.put(Pattern.compile("https?://paste\\.lucko\\.me/(\\w+)(?:\\.\\w+)?"), "https://paste.lucko.me/raw/{code}");
        pastebins.put(Pattern.compile("https?://pastebin\\.com/(\\w+)(?:\\.\\w+)?"), "https://pastebin.com/raw/{code}");
        pastebins.put(Pattern.compile("https?://gist\\.github\\.com/(\\w+/\\w+)(?:\\.\\w+/\\w+)?"), "https://gist.github.com/{code}/raw/");
        pastebins.put(Pattern.compile("https?://gitlab\\.com/snippets/(\\w+)(?:\\.\\w+)?"), "https://gitlab.com/snippets/{code}/raw");
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        try {
            String contents = event.getMessageContent();
            for (Pattern pastebin : pastebins.keySet()) {
                // All logic goes here!
                // pastebins.get(pastebin) to get the GET URL
                Matcher matcher = pastebin.matcher(event.getMessageContent());
                if (!matcher.matches()) continue;
                String urlToGet = pastebins.get(pastebin).replace("{code}", matcher.group(1));
            }
        } catch (Exception e) {
        }
    }
}