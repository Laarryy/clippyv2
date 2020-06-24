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
    private static final Pattern hastebin1 = Pattern.compile(".*https?://hastebin.com.*");
    private static final Pattern hastebin2 = Pattern.compile("https?://hasteb\\.in/(\\w+)(?:\\.\\w+)?/g");
    private static final Pattern helpchat = Pattern.compile("https?://paste\\.helpch\\.at/(\\w+)(?:\\.\\w+)?/g");
    private static final Pattern bytebin = Pattern.compile("https?://bytebin\\.lucko\\.me/(\\w+)/g");
    private static final Pattern paste = Pattern.compile("https?://paste\\.lucko\\.me/(\\w+)(?:\\.\\w+)?/g");
    private static final Pattern pastebin = Pattern.compile("https?://pastebin\\.com/(\\w+)(?:\\.\\w+)?/g");
    private static final Pattern gist = Pattern.compile("https?://gist\\.github\\.com/(\\w+/\\w+)(?:\\.\\w+/\\w+)?/g");
    private static final Pattern gitlab = Pattern.compile("https?://gitlab\\.com/snippets/(\\w+)(?:\\.\\w+)?/g");

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        CharSequence contents = event.getMessage().getContent();
            boolean hastebin1match = Pattern.matches(hastebin1.toString(), contents);

            while (event.getMessage().getContent() != null) {
                contents.
            }


        if (event.getMessageAuthor().isUser())
            if (String.valueOf(hastebin1match).equals("true")) {
                Request request = new Request.Builder()
                        .url(String.valueOf(hastebin1match))
                        .build();


            logger.debug(String.valueOf(hastebin1match));
        }
    }
}

