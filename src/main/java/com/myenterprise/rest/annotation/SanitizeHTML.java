package com.myenterprise.rest.annotation;

import java.lang.annotation.*;

/**
 * An annotation to mark a field that needs to be sanitized for HTML content.
 * This annotation is retained at runtime and is applied to fields.
 * It is used to identify fields that may contain user-provided input,
 * which should be cleaned to prevent XSS (Cross-Site Scripting) attacks.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SanitizeHTML {
}