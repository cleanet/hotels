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
package com.myenterprise.rest.annotation.sanitizehtml.dto;

/**
 * Marker interface for Data Transfer Objects (DTOs) that can be rendered as JSON
 * after sanitising any potentially unsafe HTML content.
 * <p>
 * Implementations are expected to provide a JSON representation of their
 * current state via {@link #toJsonString()} and a concise, human‑readable
 * description via {@link #getDescription()}.  The interface does not dictate
 * how the JSON is produced – that is left to the concrete class – but it
 * guarantees that both methods are available for callers.
 */
public interface JsonSanitizeDto {

    /**
     * Generates a {@code String} containing a JSON representation of the
     * implementing object's data, after any required HTML sanitization.
     *
     * @return a JSON‑formatted {@code String}; never {@code null}
     */
    String toJsonString();

    /**
     * Supplies a short, human‑readable description of the DTO.  This is useful
     * for logging, debugging or UI display, rather than for machine parsing.
     *
     * @return a brief description; may be empty but must not be {@code null}
     */
    String getDescription();
}