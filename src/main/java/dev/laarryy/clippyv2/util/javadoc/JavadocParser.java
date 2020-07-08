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
package dev.laarryy.clippyv2.util.javadoc; // Modified package name

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.javacord.api.DiscordApi;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Parses JavaDocs of a given url.
 */
public class JavadocParser {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DiscordApi api;
    private final String url;
    private final String linkUrl;

    /**
     * Creates a new Javadoc parser.
     *
     * @param api A discord api instance.
     * @param url The url of the JavaDocs.
     */
    public JavadocParser(DiscordApi api, String url, String linkUrl) {
        this.api = api;
        this.url = url.endsWith("/") ? url : url + "/";
        this.linkUrl = linkUrl.endsWith("/") ? linkUrl : linkUrl + "/";
    }

    /**
     * Gets the latest JavaDoc link.
     *
     * @param api A discord api instance.
     * @return The latest JavaDoc link.
     */
    public static CompletableFuture<String> getLatestJavaDocs(DiscordApi api) {
        return getJavadocUrl(api, "https://tobi406.github.io/luckperms-javadoc/");
    }

    // Removed getLatestCoreJavaDocs method

    private static CompletableFuture<String> getJavadocUrl(DiscordApi api, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.request().url().toString();
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, api.getThreadPool().getExecutorService());
    }

    /**
     * Gets a set with all methods.
     *
     * @return A set with all methods.
     */
    public CompletableFuture<Set<JavadocMethod>> getMethods() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getMethodsBlocking();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, api.getThreadPool().getExecutorService());
    }

    /**
     * Gets a set with all classes.
     *
     * @return A set with all classes.
     */
    public CompletableFuture<Set<JavadocClass>> getClasses() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getClassesBlocking();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, api.getThreadPool().getExecutorService());
    }

    /**
     * Creates a blocking request to get the methods.
     *
     * @return A set with all methods.
     * @throws IOException If something went wrong.
     */
    private Set<JavadocMethod> getMethodsBlocking() throws IOException {
        Request request = new Request.Builder()
                .url(url + "member-search-index.js")
                .build();

        Response response = client.newCall(request).execute();
        try (ResponseBody body = response.body()) {
            Set<JavadocMethod> methods = new HashSet<>();
            for (JsonNode node : mapper.readTree(body.string().replaceFirst("memberSearchIndex = ", ""))) {
                methods.add(new JavadocMethod(linkUrl, node)); // Use linkUrl instead of url
            }
            return methods;
        }
    }

    /**
     * Creates a blocking request to get the classes.
     *
     * @return A set with all classes.
     * @throws IOException If something went wrong.
     */
    private Set<JavadocClass> getClassesBlocking() throws IOException {
        Request request = new Request.Builder()
                .url(url + "type-search-index.js")
                .build();

        Response response = client.newCall(request).execute();
        try (ResponseBody body = response.body()) {
            Set<JavadocClass> classes = new HashSet<>();
            for (JsonNode node : mapper.readTree(body.string().replaceFirst("typeSearchIndex = ", ""))) {
                if (!node.has("url")) classes.add(new JavadocClass(linkUrl, node)); // Use link url instead of url
            }
            return classes;
        }
    }

}
