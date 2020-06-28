package dev.laarryy.clippyv2.listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class UpdateListener implements MessageCreateListener {


    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        if (ev.getMessageAuthor().isYourself()) {
            return;
        }
        if ((ev.getMessageContent().contains("1.16")) && (ev.getMessageContent().contains("update") || ev.getMessageContent().contains("luckperms") || ev.getMessageContent().contains("lp") || ev.getMessageContent().contains("when") || ev.getMessageContent().contains("luckyperms"))) {
            ev.getChannel().sendMessage("LuckPerms already works on 1.16! Please get the latest version from https://luckperms.net");
        }
        else return;
    }
}
