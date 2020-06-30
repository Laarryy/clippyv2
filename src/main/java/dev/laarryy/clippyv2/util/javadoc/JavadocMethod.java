/*       Copyright 2020 Javacord
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

/**
 * Represents a javadoc method.
 */
public class JavadocMethod {

    private final String baseUrl;
    private final String name;
    private final String className;
    private final String packageName;
    private final String url;

    /**
     * Creates a new javadoc method.
     *
     * @param baseUrl The base url of the javadocs.
     * @param node    The node with the information about the method.
     */
    public JavadocMethod(String baseUrl, JsonNode node) {
        this.baseUrl = baseUrl;
        name = node.get("l").asText();
        className = node.get("c").asText();
        packageName = node.get("p").asText();
        url = node.has("url") ? node.get("url").asText() : name.replace("(", "-").replace(")", "-");
    }

    /**
     * Gets the name of the method.
     *
     * @return The name of the method.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full name of the method, including package and class.
     *
     * @return The full name of the method.
     */
    public String getFullName() {
        return getPackageName() + "." + getClassName() + "#" + getName();
    }

    /**
     * Gets a shorter version of the name.
     *
     * @return A shorter version of the name.
     */
    public String getShortenedName() {
        String name = getName();
        if (name.length() > 40) {
            name = name.replaceAll("\\(.+\\)", "(...)");
        }
        if (name.length() > 45) {
            name = name.substring(0, 42) + "...";
        }
        return name;
    }

    /**
     * Gets name of the method's class.
     *
     * @return The name of the method's class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets name of the method's package.
     *
     * @return The name of the method's package.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the full url of the method.
     *
     * @return The full url of the method.
     */
    public String getFullUrl() {
        return baseUrl + packageName.replace(".", "/") + "/" + className + ".html#" + url;
    }

}