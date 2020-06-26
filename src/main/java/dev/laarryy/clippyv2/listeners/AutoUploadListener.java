package dev.laarryy.clippyv2.listeners;

import dev.laarryy.clippyv2.Main;
import okhttp3.*;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AutoUploadListener implements MessageCreateListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BYTEBIN_URL = "https://bytebin.lucko.me";


    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (!event.getMessageAuthor().isUser()) return;

        if (event.getMessage().getAttachments().size() == 0) return;

        event.getMessage().getAttachments().forEach(messageAttachment -> {
            if (messageAttachment.isImage()) return;
            Request request = new Request.Builder().url(messageAttachment.getUrl()).build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) return;
                if (response.body() == null) return;
                //noinspection ConstantConditions
                RequestBody requestBody = RequestBody.create(MediaType.get(response.header("Content-Type")), response.body().string());
                Request postRequest = new Request.Builder()
                        .url(BYTEBIN_URL + "/post")
                        .post(requestBody)
                        .build();
                Response bytebinResponse = client.newCall(postRequest).execute();
                if (!response.isSuccessful()) return;
                event.getChannel().sendMessage(BYTEBIN_URL + "/" + bytebinResponse.header("Location", "null")).join();

            } catch (IOException ioException) {
                logger.warn("IOException while attempting to upload a file to bytebin:");
                ioException.printStackTrace();
            }
        });
    }
}


