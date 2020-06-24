package org.laarryy.clippyv2.listeners;


import okhttp3.Request;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.laarryy.clippyv2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.*;
import java.util.regex.Pattern;

public class ErrorListener implements MessageCreateListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Pattern[] pastebins = {
            Pattern.compile("https?://hastebin\\.com/(\\w+)(?:\\.\\w+)?/g"),
            Pattern.compile("https?://hasteb\\.in/(\\w+)(?:\\.\\w+)?/g"),
            Pattern.compile("https?://paste\\.helpch\\.at/(\\w+)(?:\\.\\w+)?/g"),
            Pattern.compile("https?://bytebin\\.lucko\\.me/(\\w+)/g"),
            Pattern.compile("https?://paste\\.lucko\\.me/(\\w+)(?:\\.\\w+)?/g"),
            Pattern.compile("https?://pastebin\\.com/(\\w+)(?:\\.\\w+)?/g"),
            Pattern.compile("https?://gist\\.github\\.com/(\\w+/\\w+)(?:\\.\\w+/\\w+)?/g"),
            Pattern.compile("https?://gitlab\\.com/snippets/(\\w+)(?:\\.\\w+)?/g")
    };

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        for (Pattern pastebin : pastebins) {
            // All logic goes here!
        }
    }
}

