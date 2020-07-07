package dev.laarryy.clippyv2.util;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.time.Instant;

public class KeywordsUtil {

    private final String template;
    private final User user;
    private final Server server;
    private final String[] args;

    public KeywordsUtil(String template, User user, Server server, String... args) {
        this.template = template;
        this.user = user;
        this.server = server;
        this.args = args;
    }

    public String replace() {
        return template
                .replace("{USER}", user.getDisplayName(server))
                .replace("{USER_TAG}", user.getMentionTag())
                .replace("{TIMESTAMP_NOW}", Instant.now().toString())
                .replace("{ARGS}", String.join(" ", args))
                .replace("{USER_COUNT}", String.valueOf(server.getMemberCount()));
    }
}
