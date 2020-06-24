package org.laarryy.clippyv2.listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.laarryy.clippyv2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.File;
import java.text.Format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoUploadListener implements MessageCreateListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isUser())
        return;

        if (event.getMessage().getAttachments().contains(true));

        logger.debug(String.valueOf(event.getMessageAttachments()));

    }
}
