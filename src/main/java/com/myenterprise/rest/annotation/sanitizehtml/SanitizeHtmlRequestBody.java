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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * {@code SanitizeHtmlRequestBody} is a custom Jackson {@link JsonDeserializer}
 * for {@link String} values.
 * <p>
 * Its purpose is to sanitize HTML content during JSON deserialization in order
 * to prevent malicious input (e.g., XSS attacks). If the incoming value does not
 * contain HTML tags, it is returned as-is. Otherwise, the value is sanitized
 * using {@link SanitizerHtml}.
 * </p>
 *
 * <p>
 * This deserializer is typically used in combination with Jackson annotations
 * on REST DTO fields that may receive user-provided input.
 * </p>
 */
public class SanitizeHtmlRequestBody extends JsonDeserializer<String> {

    /**
     * Deserializes a JSON value into a {@link String}, sanitizing HTML content
     * when necessary.
     *
     * <p>
     * If the parsed value is {@code null} or does not contain HTML,
     * it is returned directly. Otherwise, the value is passed
     * through {@link SanitizerHtml#sanitizeValue(String)} before being returned.
     * </p>
     *
     * @param jsonParser the Jackson parser used to read JSON content
     * @param deserializationContext context provided by Jackson during deserialization
     * @return the original or sanitized string value
     * @throws IOException if an I/O error occurs during parsing
     */
    @Override
    public String deserialize(
            @NotNull JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException {
        String value = jsonParser.getValueAsString();
        return (value == null || SanitizerHtml.hasNotHTML(value)) ? value : SanitizerHtml.sanitizeValue(value);
    }
}
