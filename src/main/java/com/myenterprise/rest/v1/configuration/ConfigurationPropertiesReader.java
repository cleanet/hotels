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