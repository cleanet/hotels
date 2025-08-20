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
package com.myenterprise.rest.v1.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A component that reads configuration properties from the application's
 * configuration file (e.g., application.properties or application.yml).
 * The values of these properties are injected into public fields using the
 * {@link org.springframework.beans.factory.annotation.Value @Value} annotation.
 */
@Component
public class ConfigurationPropertiesReader {

    /**
     * The list of allowed HTTP headers for Cross-Origin Resource Sharing (CORS).
     * This value is injected from the 'cors.allowed-headers' property.
     */
    @Value("${cors.allowed-headers}")
    public String headers;

    /**
     * The URL mapping for which CORS is enabled.
     * This value is injected from the 'cors.allowed-mapping' property.
     */
    @Value("${cors.allowed-mapping}")
    public String mapping;

    /**
     * The list of allowed origins for Cross-Origin Resource Sharing (CORS).
     * This value is injected from the 'cors.allowed-origins' property.
     */
    @Value("${cors.allowed-origins}")
    public String origins;

    /**
     * The warning message for logging HTML sanitization events.
     * This value is injected from the 'logging.sanitizerHTML.message' property.
     */
    @Value("${logging.sanitizerHTML.message}")
    public String sanitizeWarningMessage;
}