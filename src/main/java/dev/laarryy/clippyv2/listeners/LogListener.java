package dev.laarryy.clippyv2.listeners;

import dev.laarryy.clippyv2.Constants;
import dev.laarryy.clippyv2.storage.LogStorage;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class LogListener implements MessageCreateListener {
    LogStorage logStore = new LogStorage();
    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        if (!ev.getChannel().getIdAsString().equals(Constants.CHANNEL_LOGS)) {
            String message = ("Message in channel: " + ev.getChannel() + " sent by  " + ev.getMessageAuthor() + ": " + ev.getMessage().getContent());
            logStore.sendToLog(message);
        }
    }
}
