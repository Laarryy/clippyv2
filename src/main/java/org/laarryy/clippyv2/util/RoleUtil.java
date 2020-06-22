package org.laarryy.clippyv2.util;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.laarryy.clippyv2.Constants;

public class RoleUtil {

    public static boolean isStaffMention;

    public RoleUtil(Message message, Server server) {
        boolean mentionStaff = message.getMentionedUsers().get(0).getRoles(server).stream().anyMatch(role -> role.getIdAsString().equals(Constants.ROLE_STAFF));
                if (mentionStaff) {
                    isStaffMention = true;
                }
                else isStaffMention = false;
    }
}

