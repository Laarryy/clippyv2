package dev.laarryy.clippyv2.util;

import dev.laarryy.clippyv2.Constants;
import org.javacord.api.entity.channel.Channel;

public class ChannelUtil {
    public static boolean isStaffChannel(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_STAFF)
            || channel.getIdAsString().equals(Constants.CHANNEL_BOT)
            || channel.getIdAsString().equals(Constants.CHANNEL_PEBKAC)
        ) {
            return true;
        } else return false;
    }
    public static boolean isNonPublicChannel(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_PATREONS)
            || channel.getIdAsString().equals(Constants.CHANNEL_HELPFUL)
            || channel.getIdAsString().equals(Constants.CHANNEL_STAFF)
            || channel.getIdAsString().equals(Constants.CHANNEL_BOT)
            || channel.getIdAsString().equals(Constants.CHANNEL_PEBKAC)
        ) {
            return true;
        } else return false;
    }
    public static boolean isOffTopic(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC)) {
            return true;
        } else return false;
    }
}
