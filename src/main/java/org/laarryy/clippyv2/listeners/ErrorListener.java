package org.laarryy.clippyv2.listeners;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.laarryy.clippyv2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import java.util.regex.Pattern;

public class ErrorListener implements MessageCreateListener {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final HashMap<Pattern, String> pastebins = new HashMap<>();
    private final ArrayList<Check> checks = new ArrayList<>();

    public ErrorListener() {
        // mcpaste.io will not work, because it has a massive delay on actually sending data
        pastebins.put(Pattern.compile("(?:.*)?https?://hastebin\\.com/(\\w+)(?:.*)?"), "https://hastebin.com/raw/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://hasteb\\.in/(\\w+)(?:.*)?"), "https://hasteb.in/raw/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://paste\\.helpch\\.at/(\\w+)(?:.*)?"), "https://paste.helpch.at/raw/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://bytebin\\.lucko\\.me/(\\w+)"), "https://bytebin.lucko.me/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://paste\\.lucko\\.me/(\\w+)(?:.*)?"), "https://paste.lucko.me/raw/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://pastebin\\.com/(\\w+)(?:.*)?"), "https://pastebin.com/raw/{code}");
        pastebins.put(Pattern.compile("(?:.*)?https?://gist\\.github\\.com/(\\w+/\\w+)(?:.*)?"), "https://gist.github.com/{code}/raw/");
        pastebins.put(Pattern.compile("(?:.*)?https?://gitlab\\.com/snippets/(\\w+)(?:.*)?"), "https://gitlab.com/snippets/{code}/raw");
        pastebins.put(Pattern.compile("(?:.*)?https?://paste\\.helpch\\.at/(\\w+)(?:.*)?"), "https://paste.helpch.at/{code}");


        // Add new error checks here
        // and make sure that less specific checks are last,
        // especially the very broad check for "cannot connect to mysql server"
        // because checks stop once the first successful one is found, in order
        // to stop embed spam for every single check on a log.

        checks.add(new Check("MySQL Exceeded Max Connections",
                "https://github.com/lucko/LuckPerms/wiki/Storage-system-errors#mysql-exceeded-max-connections",
                Pattern.compile(("(?:.*)?Caused by: com\\.mysql\\.jdbc\\.exceptions\\.jdbc4\\.MySQLSyntaxErrorException: User '.*' has exceeded the 'max_user_connections' resource \\(current value: \\w+\\)(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)));
        checks.add(new Check("MySQL \"No Operations allowed after connection closed\" error",
                "https://github.com/lucko/LuckPerms/wiki/Storage-system-errors#mysql-no-operations-allowed-after-connection-closed-error",
                Pattern.compile(("(?:.*)?me\\.lucko\\.luckperms\\.lib\\.hikari\\.pool\\.PoolBase - luckperms-hikari - Failed to validate connection me\\.lucko\\.luckperms\\.lib\\.mysql\\.jdbc\\.JDBC4Connection@\\w+ \\(No operations allowed after connection closed\\.\\)(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE),
                Pattern.compile(("(?:.*)?me\\.lucko\\.luckperms\\.lib\\.hikari\\.pool\\.PoolBase - luckperms-hikari- Failed to validate connection me\\.lucko\\.luckperms\\.lib\\.mariadb\\.MariaDbConnection@\\w+ \\(\\w+ cannot be called on a closed connection\\)(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)));
        checks.add(new Check("MySQL SSL Error",
                "https://github\\.com/lucko/LuckPerms/wiki/Storage-system-errors#mysql-ssl-errors",
                Pattern.compile(("(?:.*)?(Establishing SSL connection without server's identity verification is not recommended)(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)));
        checks.add(new Check("LuckPerms cannot connect to your database server",
                "https://github.com/lucko/LuckPerms/wiki/Storage-system-errors#luckperms-cannot-connect-to-my-mysql-server",
                Pattern.compile(("(?:.*)?java\\.util\\.concurrent\\.CompletionException: java\\.sql\\.SQLTransientConnectionException: luckperms(?:-hikari)? - Connection is not available, request timed out after(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE),
                Pattern.compile(("(?:.*)?java\\.sql\\.SQLTransientConnectionException: luckperms(?:-hikari)? - Connection is not available, request timed out after (?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE),
                Pattern.compile(("(?:.*)?luckperms - Failed to validate connection com\\.mysql\\.jdbc\\.JDBC4Connection@\\w+ \\(Communications link failure\\)(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE),
                Pattern.compile(("(?:.*)?The last packet successfully received from the server was \\d+ milliseconds ago\\. The last packet sent successfully to the server was \\d+ milliseconds ago\\.(?:.*)?"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)));
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String contents = event.getMessageContent();
        for (Pattern pastebin : pastebins.keySet()) {
            // All logic goes here!
            // pastebins.get(pastebin) to get the GET URL
            Matcher matcher = pastebin.matcher(event.getMessageContent());
            if (!matcher.matches()) continue;
            logger.debug(matcher.toString());
            String urlToGet = pastebins.get(pastebin).replace("{code}", matcher.group(1));
            Request request = new Request.Builder()
                .url(urlToGet)
                .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) continue;
                String content = response.body().string();
                response.close();
                for (Check check : checks) {
                    boolean matched = false;
                    for (Pattern regex : check.getPatterns()) {
                        if (regex.matcher(content).matches())
                            matched = true;
                        logger.debug(String.valueOf(matched));
                    }

                    if (!matched) continue;
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(check.getTitle())
                            .addField("Read more:", check.getUrl())
                            .setFooter(matcher.group() + " | Sent by " + event.getMessageAuthor().getDisplayName())
                            .setColor(Color.RED));
                    return;
                }
            } catch (IOException | NullPointerException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    static class Check {
        private final Pattern[] patterns;
        private final String title;
        private final String url;

        public Check(String title, String url, Pattern... patterns) {
            this.patterns = patterns;
            this.title = title;
            this.url = url;
        }

        public Pattern[] getPatterns() {
            return patterns;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }
}