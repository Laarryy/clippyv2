package org.laarryy.clippyv2.listeners;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.laarryy.clippyv2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoUploadListener implements MessageCreateListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isUser()) return;

        if (event.getMessage().getAttachments().size() == 0) return;

        event.getMessage().getAttachments().forEach(messageAttachment -> {
            if (messageAttachment.isImage()) return;
            Request request = new Request.Builder().url(messageAttachment.getUrl()).build();
            try {
                logger.debug(client.newCall(request).execute().body().string()); // TODO ensure body != null
            } catch (IOException ioException) {
                logger.warn("IOException while attempting to reach " + messageAttachment.getUrl() + ": " + ioException.getMessage());
            }
        });

    }
}
