package com.myenterprise.rest.component;

import com.myenterprise.rest.v1.configuration.ConfigurationPropertiesReader;
import org.jetbrains.annotations.NotNull;
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
public class SanitizerHTMLListener implements HtmlChangeListener<Class<?>> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SanitizerHTMLListener.class.getName());
    /**
     * Configuration object to store and manage logger details for sanitization events.
     */
    private final SanitizerHTMLLoggerConfiguration sanitizerHTMLLoggerConfiguration;

    /**
     * Constructs a new SanitizerHTMLListener.
     *
     * @param loggerConfiguration a {@link SanitizerHTMLLoggerConfiguration} instance to be used for logging.
     * @param propertiesConfiguration a {@link ConfigurationPropertiesReader} instance for reading properties.
     */
    @Autowired
    public SanitizerHTMLListener(@NotNull SanitizerHTMLLoggerConfiguration loggerConfiguration, @NotNull ConfigurationPropertiesReader propertiesConfiguration){
        this.sanitizerHTMLLoggerConfiguration = new SanitizerHTMLLoggerConfiguration(propertiesConfiguration)
                .setFieldName(loggerConfiguration.getFieldName())
                .setControllerClassName(loggerConfiguration.getControllerClassName())
                .setControllerMethodName(loggerConfiguration.getControllerMethodName())
                .setModelClassName(loggerConfiguration.getModelClassName());
    }

    /**
     * Registers and logs all the rejected tags and attributes.
     * <p>
     * This method checks if there are any discarded elements or attributes.
     * If so, it logs a warning message containing the details of the rejection
     * and then clears the internal lists of rejected items.
     */
    public void register(){
        if ( sanitizerHTMLLoggerConfiguration.getRejectedAttributes().isEmpty() &&
                sanitizerHTMLLoggerConfiguration.getRejectedTags().isEmpty() ) return;

        String message = sanitizerHTMLLoggerConfiguration.toString();
        LOGGER.warning(message);
        sanitizerHTMLLoggerConfiguration.clearRejectedTags();
        sanitizerHTMLLoggerConfiguration.clearRejectedAttributes();
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
        sanitizerHTMLLoggerConfiguration.setRejectedTags(tagName);
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
        sanitizerHTMLLoggerConfiguration.setRejectedAttributes(tagName, attributes);
    }
}