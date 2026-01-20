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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * {@code SanitizeHtmlResponse} is a custom Jackson {@link JsonSerializer}
 * for {@link String} values.
 * <p>
 * This serializer sanitizes HTML content before writing it to JSON output.
 * If the value is {@code null} or does not contain HTML tags, it is written
 * unchanged. Otherwise, the value is sanitized using
 * {@link SanitizerHtml#sanitizeValue(String)} to prevent the serialization
 * of potentially unsafe HTML content.
 * </p>
 *
 * <p>
 * This serializer is typically used on REST response DTO fields that may
 * contain user-generated content.
 * </p>
 */
public class SanitizeHtmlResponse extends JsonSerializer<String> {

    /**
     * Serializes a {@link String} value into JSON, sanitizing HTML content
     * when necessary.
     *
     * <p>
     * If the value is {@code null} or does not contain HTML,
     * it is written directly to the JSON output. Otherwise,
     * the value is sanitized before being written.
     * </p>
     *
     * @param value the string value to serialize
     * @param generator the {@link JsonGenerator} used to write JSON content
     * @param provider the serializer provider
     * @throws IOException if an I/O error occurs during JSON generation
     */
    @Override
    public void serialize(
            String value,
            JsonGenerator generator,
            SerializerProvider provider
    ) throws IOException {
        if (value == null || SanitizerHtml.hasNotHTML(value)) {
            generator.writeString(value);
            return;
        }
        generator.writeString(SanitizerHtml.sanitizeValue(value));
    }
}
