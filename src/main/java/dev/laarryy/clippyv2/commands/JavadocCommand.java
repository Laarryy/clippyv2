/*        Copyright 2020 Javacord
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */
// Adopted from https://github.com/Javacord/Javacord-Bot
package dev.laarryy.clippyv2.commands; // Change package name

// Remove some imports

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.javadoc.JavadocClass;
import dev.laarryy.clippyv2.util.javadoc.JavadocMethod;
import dev.laarryy.clippyv2.util.javadoc.JavadocParser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// Modified javadoc
/**
 * The !javadocs command which is used to show links to Javacord's JavaDocs.
 */
public class JavadocCommand implements CommandExecutor {

    /**
     * The parameters that indicate searching for class names only.
     */
    private static final Set<String> classParams = new HashSet<>(Arrays.asList("classes", "class", "c"));

    /**
     * The parameters that indicate searching for method names only.
     */
    private static final Set<String> methodParams = new HashSet<>(Arrays.asList("methods", "method", "m"));

    /**
     * The parameters that indicate also searching internal packages and the core docs.
     */
    private static final Set<String> includeAllParams = new HashSet<>(Arrays.asList("all", "a"));

    /**
     * The string to which the url link will be called
     */
    private static final String linkUrlString = "https://javadoc.io/doc/net.luckperms/api/latest/";

    /**
     * Executes the {@code !docs} command.
     *
     * @param server  The server where the command was issued.
     * @param channel The channel where the command was issued.
     * @param message The message the command was issued in.
     * @param args    The arguments given to the command.
     * @throws IOException If the Javacord icon stream cannot be closed properly.
     */
    // Modified alias and usage
    @Command(aliases = {"!javadoc", "!javadocs"}, async = true, usage = "!javadoc [method|class] <search>",
            description = "Shows a link to the JavaDoc or searches through it")
    public void onCommand(Server server, TextChannel channel, Message message, String[] args) throws IOException {
        // Removed Discord API Server parts
        // Removed InputStream for icon
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("JavaDoc Search") // Add setTitle
                .setColor(new Color(0x13C108))
                .setFooter("LuckPerms JavaDoc", "https://luckperms.net/logo.png");
                // Remove setColor call
        if (args.length == 0) { // Just give an overview
            // Modified urls and removed fields
            embed.addField("JavaDoc", "https://javadoc.io/doc/net.luckperms/api/")
                    .addField("Hint", "You can search the docs using `!javadoc [method|class] <search>`");
        } else if (args.length == 1) { // Basic search - methods without internals
            populateMethods(channel.getApi(), embed, args[0], false);
        } else { // Extended search
            if (classParams.contains(args[0])) { // Search for a class
                boolean searchAll = args.length > 2 && includeAllParams.contains(args[1]);
                String searchString = String.join(" ", Arrays.copyOfRange(args, searchAll ? 2 : 1, args.length));
                populateClasses(channel.getApi(), embed, searchString, searchAll);
            } else if (methodParams.contains(args[0])) { // Search for a method
                boolean searchAll = args.length > 2 && includeAllParams.contains(args[1]);
                String searchString = String.join(" ", Arrays.copyOfRange(args, searchAll ? 2 : 1, args.length));
                populateMethods(channel.getApi(), embed, searchString, searchAll);
            } else { // Search for a method
                boolean searchAll = includeAllParams.contains(args[0]);
                String searchString = String.join(" ", Arrays.copyOfRange(args, searchAll ? 1 : 0, args.length));
                populateMethods(channel.getApi(), embed, searchString, searchAll);
            }
        }
        // Removed call to CommandCleanupListener as it doesn't exist in this project
        channel.sendMessage(embed).join();
    }

    /**
     * Populates the methods field inside the given embed.
     *
     * @param api          A discord api instance.
     * @param embed        The embed to populate.
     * @param searchString A search string.
     */
    private void populateMethods(DiscordApi api, EmbedBuilder embed, String searchString, boolean includeAll) {
        CompletableFuture<Set<JavadocMethod>> apiMethods = JavadocParser.getLatestJavaDocs(api)
                .thenApply(urlString -> new JavadocParser(api, urlString, linkUrlString)) // add linkUrlString param
                .thenCompose(JavadocParser::getMethods);
        // Removed coreMethods

        Map<String, List<JavadocMethod>> methodsByClass = apiMethods.join().stream()
                // Removed thenCombine
                .filter(method -> method.getFullName().toLowerCase().contains(searchString.toLowerCase()))
                .filter(method -> {
                    String packageName = method.getPackageName();
                    return includeAll || !(packageName.endsWith(".internal") || packageName.contains(".internal."));
                })
                .sorted(Comparator.comparingInt(method -> method.getName().length()))
                .collect(Collectors.groupingBy(JavadocMethod::getClassName));


        if (methodsByClass.isEmpty()) {
            embed.setTitle("Methods");
            embed.setDescription("No matching methods found!");
            return;
        }

        int totalTextCount = 25; // the maximum tracker string length
        List<Map.Entry<String, List<JavadocMethod>>> entries = new ArrayList<>(methodsByClass.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey, String::compareToIgnoreCase));
        int classesAmount = entries.size();
        for (int classIndex = 0; classIndex < classesAmount; classIndex++) {
            Map.Entry<String, List<JavadocMethod>> entry = entries.get(classIndex);
            List<JavadocMethod> methods = entry.getValue();
            methods.sort(Comparator.comparing(JavadocMethod::getShortenedName, String::compareToIgnoreCase));
            StringBuilder methodsBuilder = new StringBuilder();
            int methodsAmount = methods.size();
            for (int methodIndex = 0; methodIndex < methodsAmount; methodIndex++) {
                JavadocMethod method = methods.get(methodIndex);
                StringBuilder methodBuilder = new StringBuilder()
                        .append("• [")
                        .append(method.getShortenedName())
                        .append("](")
                        .append(method.getFullUrl())
                        .append(")\n");
                int nextMoreSize = methodIndex == (methodsAmount - 1)
                        ? 0
                        : 11 + (int) (Math.log10(methodsAmount - methodIndex - 1) + 1);
                if ((methodsBuilder.length() + methodBuilder.length() + nextMoreSize) <= 1024) {
                    methodsBuilder.append(methodBuilder);
                } else {
                    methodsBuilder.append("• ").append(methodsAmount - methodIndex).append(" more ...");
                    break;
                }
            }
            int nextMoreSize = classIndex == (classesAmount - 1)
                    ? 0
                    : 57 + (int) (Math.log10(classesAmount - classIndex - 1) + 1);
            String className = entry.getKey();
            if ((totalTextCount + className.length() + methodsBuilder.length() + nextMoreSize) <= 6000) {
                embed.addField(className, methodsBuilder.toString());
                totalTextCount += className.length() + methodsBuilder.length();
            } else {
                embed.addField(String.format("And **%d** more classes ...", classesAmount - classIndex),
                        "Maybe try a less generic search?");
                break;
            }
        }
    }

    // Removed unionOf method

    /**
     * Populates the classes field inside the given embed.
     *
     * @param api          A discord api instance.
     * @param embed        The embed to populate.
     * @param searchString A search string.
     */
    private void populateClasses(DiscordApi api, EmbedBuilder embed, String searchString, boolean includeAll) {
        CompletableFuture<Set<JavadocClass>> apiClasses = JavadocParser.getLatestJavaDocs(api)
                .thenApply(urlString -> new JavadocParser(api, urlString, linkUrlString)) // add linkUrlString param
                .thenCompose(JavadocParser::getClasses);
        // Removed coreClasses

        List<JavadocClass> classes = apiClasses.join().stream()
                // Removed thenCombine
                .filter(clazz -> clazz.getName().toLowerCase().contains(searchString.toLowerCase()))
                .filter(clazz -> {
                    String packageName = clazz.getPackageName();
                    return includeAll || !(packageName.endsWith(".internal") || packageName.contains(".internal."));
                })
                .sorted(Comparator.comparingInt(clazz -> clazz.getName().length()))
                .collect(Collectors.toList());

        embed.setTitle("Classes");
        if (classes.isEmpty()) {
            embed.setDescription("No matching classes found!");
            return;
        }

        StringBuilder strBuilder = new StringBuilder();
        int counter = 0;
        for (JavadocClass clazz : classes) {
            if (strBuilder.length() > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append("[")
                    .append(clazz.getName())
                    .append("](")
                    .append(clazz.getFullUrl())
                    .append(")");
            counter++;
            if (strBuilder.length() > 1950) { // Prevent hitting the description size limit
                break;
            }
        }

        if (classes.size() - counter > 0) {
            strBuilder.append("\nand ").append(classes.size() - counter).append(" more ...");
        }

        embed.setDescription(strBuilder.toString());
    }

}