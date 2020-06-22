package org.laarryy.clippyv2.util;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.laarryy.clippyv2.Constants;

public class RoleUtil {
    public static boolean hasStaffMention(Message message) {
        if (!message.getServer().isPresent()) throw new IllegalStateException("Message does not have a server");
        return message.getMentionedUsers().get(0).getRoles(message.getServer().get()).stream()
                .anyMatch(role -> role.getIdAsString().equals(Constants.ROLE_STAFF));
    }
}

