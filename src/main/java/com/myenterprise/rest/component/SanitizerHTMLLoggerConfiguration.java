/*
 *   MIT License
 *
 *  Copyright (c) 2025 cleannet
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.myenterprise.rest.component;

import com.myenterprise.rest.v1.configuration.ConfigurationPropertiesReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * A configuration component for logging sanitization events.
 * <p>
 * This class holds the details of a sanitization event, including the field name,
 * model, controller, and any rejected HTML tags or attributes. It is designed to
 * format and store this information for logging purposes, providing a clear
 * record of what was sanitized.
 * </p>
 * <p>
 * The class uses a {@link ConfigurationPropertiesReader} to build a log message
 * based on a configured template.
 */
@Component
public class SanitizerHTMLLoggerConfiguration {

    /**
     * The configuration properties reader.
     */
    private final ConfigurationPropertiesReader configurationProperties;
    /**
     * The name of the field that was sanitized.
     */
    private String fieldName;
    /**
     * The name of the model class containing the sanitized field.
     */
    private String modelClassName;
    /**
     * The name of the controller class that handled the request.
     */
    private String controllerClassName;
    /**
     * The name of the controller method that was executed.
     */
    private String controllerMethodName;
    /**
     * A list of HTML tags that were rejected during sanitization.
     */
    private ArrayList<String> rejectedTags = new ArrayList<>();
    /**
     * A map of HTML tags to their rejected attributes.
     */
    private HashMap<String, String[]> rejectedAttributes = new HashMap<>();

    /**
     * Constructs a new SanitizerHTMLLoggerConfiguration with a configuration properties reader.
     *
     * @param configurationProperties the configuration properties reader.
     */
    @Autowired
    public SanitizerHTMLLoggerConfiguration( ConfigurationPropertiesReader configurationProperties ){
        this.configurationProperties = configurationProperties;
    }

    /**
     * Generates a formatted log message string.
     *
     * @return a string with the log message, with placeholders replaced by the configuration values.
     */
    @Override
    public String toString(){
        return configurationProperties.sanitizeWarningMessage
                .replace("{fieldName}", fieldName)
                .replace("{modelClassName}", modelClassName)
                .replace("{controllerClassName}", controllerClassName)
                .replace("{controllerMethodName}", controllerMethodName)
                .replace("{rejectedTags}", this.getRejectedTags())
                .replace("{rejectedAttributes}", this.getRejectedAttributes());
    }

    /**
     * Gets the name of the sanitized field.
     *
     * @return the field name.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the name of the sanitized field.
     *
     * @param fieldName the field name.
     * @return this configuration object.
     */
    public SanitizerHTMLLoggerConfiguration setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    /**
     * Gets the name of the model class.
     *
     * @return the model class name.
     */
    public String getModelClassName() {
        return modelClassName;
    }

    /**
     * Sets the name of the model class.
     *
     * @param modelClassName the model class name.
     * @return this configuration object.
     */
    public SanitizerHTMLLoggerConfiguration setModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
        return this;
    }

    /**
     * Gets the name of the controller class.
     *
     * @return the controller class name.
     */
    public String getControllerClassName() {
        return controllerClassName;
    }

    /**
     * Sets the name of the controller class.
     *
     * @param controllerClassName the controller class name.
     * @return this configuration object.
     */
    public SanitizerHTMLLoggerConfiguration setControllerClassName(String controllerClassName) {
        this.controllerClassName = controllerClassName;
        return this;
    }

    /**
     * Gets the name of the controller method.
     *
     * @return the controller method name.
     */
    public String getControllerMethodName() {
        return controllerMethodName;
    }

    /**
     * Sets the name of the controller method.
     *
     * @param controllerMethodName the controller method name.
     * @return this configuration object.
     */
    public SanitizerHTMLLoggerConfiguration setControllerMethodName(String controllerMethodName) {
        this.controllerMethodName = controllerMethodName;
        return this;
    }

    /**
     * Gets a string representation of the rejected tags.
     *
     * @return a string representing the rejected tags, or an empty string if none were rejected.
     */
    public String getRejectedTags() {
        if ( rejectedTags.isEmpty() ) return "";
        return rejectedTags.toString();
    }

    /**
     * Adds a rejected tag to the list.
     *
     * @param tagName the name of the rejected tag.
     */
    public void setRejectedTags(@NotNull String tagName) {
        this.rejectedTags.add(tagName);
    }

    /**
     * Clears the list of rejected tags.
     */
    public void clearRejectedTags() {
        this.rejectedTags = new ArrayList<>();
    }

    /**
     * Gets a string representation of the rejected attributes.
     *
     * @return a formatted string of rejected attributes, or an empty string if none were rejected.
     */
    public String getRejectedAttributes() {
        if ( rejectedAttributes.isEmpty() ) return "";
        return rejectedAttributes
                .keySet()
                .stream()
                .map(
                        key -> key + "=" + Arrays.toString(rejectedAttributes.get(key))
                )
                .collect(
                        Collectors.joining(", ", "{", "}")
                );
    }

    /**
     * Adds rejected attributes for a specific tag.
     *
     * @param tagName the name of the tag.
     * @param attributes an array of rejected attribute names.
     */
    public void setRejectedAttributes(@NotNull String tagName, String[] attributes) {
        this.rejectedAttributes.put(tagName, attributes);
    }

    /**
     * Clears the map of rejected attributes.
     */
    public void clearRejectedAttributes() {
        this.rejectedAttributes = new HashMap<>();
    }
}