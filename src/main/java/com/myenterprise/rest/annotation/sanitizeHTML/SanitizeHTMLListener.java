/*
 *   MIT License
 *
 *  Copyright (c) 2025 cleanet
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
package com.myenterprise.rest.annotation.sanitizeHTML;

import org.owasp.html.HtmlChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * A component that acts as a listener for discarded HTML tags and attributes during sanitization.
 * <p>
 * This class implements {@link HtmlChangeListener} to capture information about
 * any HTML elements or attributes that are removed by the sanitization policy. It
 * uses a logger configuration to store and report these discarded items,
 * providing a mechanism to monitor potential XSS attempts or unexpected content.
 * </p>
 * <p>
 * The listener is typically registered with a sanitizer to receive notifications
 * about policy violations. It then logs these violations based on a provided
 * configuration.
 */
@Component
public class SanitizeHTMLListener implements HtmlChangeListener<Class<?>> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SanitizeHTMLListener.class.getName());
    /**
     * Configuration object to store and manage logger details for sanitization events.
     */
    private SanitizeHTMLLoggerConfiguration sanitizeHTMLLoggerConfiguration;

    /**
     * Constructs a new SanitizeHTMLListener.
     */
    @Autowired
    public SanitizeHTMLListener(){}

    public void setSanitizerHTMLLoggerConfiguration( SanitizeHTMLLoggerConfiguration sanitizeHTMLLoggerConfiguration){
        this.sanitizeHTMLLoggerConfiguration = sanitizeHTMLLoggerConfiguration;
    }

    /**
     * Registers and logs all the rejected tags and attributes.
     * <p>
     * This method checks if there are any discarded elements or attributes.
     * If so, it logs a warning message containing the details of the rejection
     * and then clears the internal lists of rejected items.
     */
    public void register(){
        if ( sanitizeHTMLLoggerConfiguration.getRejectedAttributes().isEmpty() &&
                sanitizeHTMLLoggerConfiguration.getRejectedTags().isEmpty() ) return;

        String message = sanitizeHTMLLoggerConfiguration.toString();
        LOGGER.warning(message);
        sanitizeHTMLLoggerConfiguration.clearRejectedTags();
        sanitizeHTMLLoggerConfiguration.clearRejectedAttributes();
    }

    /**
     * Called by the sanitizer when a tag is discarded.
     * Add in the rejected tags list the element current rejected
     *
     * @param sanitizeHTMLAspect the class that performed the sanitization.
     * @param tagName the name of the discarded tag.
     */
    @Override
    public void discardedTag(Class<?> sanitizeHTMLAspect, String tagName) {
        sanitizeHTMLLoggerConfiguration.setRejectedTags(tagName);
    }

    /**
     * Called by the sanitizer when one or more attributes are discarded from a tag.
     *
     * @param sanitizeHTMLAspect the class that performed the sanitization.
     * @param tagName the name of the tag from which the attributes were discarded.
     * @param attributes an array of the names of the discarded attributes.
     */
    @Override
    public void discardedAttributes(Class<?> sanitizeHTMLAspect, String tagName, String... attributes) {
        sanitizeHTMLLoggerConfiguration.setRejectedAttributes(tagName, attributes);
    }
}