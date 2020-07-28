package dev.laarryy.clippyv2.util;

import dev.laarryy.clippyv2.Constants;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;

public class RoleUtil {
    public static boolean hasStaffMention(Message message) {
        if (!message.getServer().isPresent()) throw new IllegalStateException("Message does not have a server");
        return message.getMentionedUsers().get(0).getRoles(message.getServer().get()).stream()
                .anyMatch(role -> role.getIdAsString().equals(Constants.ROLE_STAFF));
    }

    public static Boolean isStaff(User user, Server server) {
        List<Role> userRoles = user.getRoles(server);
        for (Role role : userRoles) {
            String roleId = role.getIdAsString();
            if ((roleId.equals(Constants.ROLE_STAFF))) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isPatreon(List<Role> roles) {
        for (Role role : roles) {
            String roleId = role.getIdAsString();
            if ((roleId.equals(Constants.ROLE_PATREON))) {
                return true;
            } else ;
        }
        return false;
    }
}

