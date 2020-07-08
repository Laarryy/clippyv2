/*       Copyright 2020 Javacord
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
// Adopted from https://github.com/Javacord/Javacord-Bot
package dev.laarryy.clippyv2.util.javadoc; // Modified package name

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a javadoc class.
 */
public class JavadocClass {

    private final String baseUrl;
    private final String name;
    private final String packageName;

    /**
     * Creates a new javadoc class.
     *
     * @param baseUrl The base url of the javadocs.
     * @param node    The node with the information about the class.
     */
    public JavadocClass(String baseUrl, JsonNode node) {
        this.baseUrl = baseUrl;
        name = node.get("l").asText();
        packageName = node.get("p").asText();
    }

    /**
     * Gets the name of the class.
     *
     * @return The name of the class.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets name of the class' package.
     *
     * @return The name of the class' package.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the full url of the class.
     *
     * @return The full url of the class.
     */
    public String getFullUrl() {
        return baseUrl + packageName.replace(".", "/") + "/" + name + ".html";
    }

}
