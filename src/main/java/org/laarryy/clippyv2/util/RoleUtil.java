package org.laarryy.clippyv2.util;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.laarryy.clippyv2.Constants;

public class RoleUtil {

    private Boolean isStaff;

    public RoleUtil(Message message, Server server) {
        boolean isStaff = message.getMentionedUsers().get(0).getRoles(server).stream().anyMatch(role -> {return role.getIdAsString().equals(Constants.ROLE_STAFF);
        });
                if (isStaff == true) {

                }
                else return;
        ;
    }
}
