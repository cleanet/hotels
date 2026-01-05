package com.myenterprise.rest.annotation.sanitizeHTML;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static com.myenterprise.rest.annotation.sanitizeHTML.SanitizePolicy.CKEDITOR_POLICY;

public class SanitizerHTML {

    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>(1024);

    @NotNull
    public static String sanitizeValue(String value ){

        String sanitized = CACHE.computeIfAbsent(
                value,
                text -> CKEDITOR_POLICY.sanitize(text, null, SanitizeHTMLSerializer.class)
        );

        return CKEDITOR_POLICY.sanitize(
                sanitized,
                null,
                SanitizerHTML.class
        );

    }
}
