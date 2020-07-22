package dev.laarryy.clippyv2.listeners;

import dev.laarryy.clippyv2.storage.LogStorage;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class LogListener implements MessageCreateListener {
    LogStorage logStore = new LogStorage();
    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        String message = (ev.getChannel() + ">> " + ev.getMessageAuthor() + ": " + ev.getMessage().getContent());
        logStore.sendToLog(message);
    }
}
