/*
 *   MIT License
 *
 *  Copyright (c) 2026 cleanet
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
package com.myenterprise.rest.annotation.sanitizehtml;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.myenterprise.rest.annotation.sanitizehtml.SanitizePolicy.HTML_POLICY;

/**
 * {@code SanitizerHtml} provides utility methods for sanitizing HTML content.
 * <p>
 * This class applies a predefined sanitization policy ({@link SanitizePolicy#HTML_POLICY})
 * to input strings in order to remove or neutralize potentially unsafe HTML.
 * </p>
 *
 * <p>
 * To improve performance, an internal cache is used to store previously
 * sanitized values. The cache is thread-safe and suitable for concurrent access.
 * </p>
 *
 * <p>
 * This class is intended to be used by Jackson serializers/deserializers
 * and other infrastructure components that need to sanitize user-provided
 * HTML content.
 * </p>
 */
public class SanitizerHtml {

    /**
     * Thread-safe cache that stores sanitized values to avoid repeating
     * expensive sanitization operations for the same input.
     */
    private static final ConcurrentHashMap<String, String> CACHE =
            new ConcurrentHashMap<>(1024);

    /**
     * Regular expression pattern used to detect the presence of HTML tags
     * within a string.
     *
     * <p>
     * This pattern is intentionally simple and is <strong>not</strong> intended
     * to fully parse or validate HTML. Its sole purpose is to provide a fast,
     * lightweight heuristic to determine whether a string potentially contains
     * HTML markup.
     * </p>
     *
     * <p>
     * A full HTML parser would introduce unnecessary complexity and performance
     * overhead for this use case. The actual responsibility for proper HTML
     * sanitization and security enforcement is delegated to
     * {@link SanitizePolicy#HTML_POLICY}, which is designed to safely handle
     * malformed or complex HTML input.
     * </p>
     *
     * <p>
     * This pattern should therefore be understood as an optimization and
     * pre-check mechanism, not as a security boundary.
     * </p>
     */
    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");

    /**
     * Sanitizes the given string value using the configured HTML sanitization policy.
     *
     * <p>
     * The method first retrieves a cached sanitized value if available.
     * If not present, it sanitizes the original value and stores the result
     * in the cache. A second sanitization pass is then applied to the cached
     * value to ensure consistency and safety.
     * </p>
     *
     * @param value the input string to sanitize; must not be {@code null}
     * @return the sanitized string, never {@code null}
     */
    @NotNull
    public static String sanitizeValue(String value) {

        return CACHE.computeIfAbsent(
                value,
                text -> HTML_POLICY.sanitize(text, null, SanitizeHtmlResponse.class)
        );
    }

    /**
     * Determines whether the given string does not contain HTML markup.
     *
     * <p>
     * This method checks the input string against a predefined HTML pattern.
     * If no HTML elements or tags are detected, the method returns {@code true};
     * otherwise, it returns {@code false}.
     * </p>
     *
     * @param value the string to evaluate; must not be {@code null}
     * @return {@code true} if the string does not contain HTML, {@code false} otherwise
     */
    public static boolean hasNotHTML(@NotNull String value) {
        return !HTML_PATTERN.matcher(value).find();
    }
}
