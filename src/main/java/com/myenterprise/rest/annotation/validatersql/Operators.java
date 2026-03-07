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
package com.myenterprise.rest.annotation.validatersql;

/**
 * Enumeration of the comparison operators supported by the RSQL validation
 * framework used throughout the project.
 *
 * <p>Each constant corresponds to a symbolic operator that can appear in an
 * RSQL query string. The {@link com.myenterprise.rest.annotation.validatersql.ValidateRsql}
 * annotation allows you to specify which of these operators are permitted for a
 * particular request parameter.</p>
 *
 * <p>The naming follows a clear, self‑explanatory style so that junior developers
 * can easily understand the purpose of each value without needing to refer to the
 * RSQL specification.</p>
 */
public enum Operators {

    /**
     * Represents the {@code =in=} operator, which tests whether a field’s value
     * is contained within a supplied collection.
     */
    IN,

    /**
     * Represents the {@code =out=} operator, which tests whether a field’s value
     * is **not** contained within a supplied collection.
     */
    NOT_IN,

    /**
     * Equality operator ({@code ==}). Checks if a field’s value exactly matches
     * the provided value.
     */
    EQUAL,

    /**
     * Inequality operator ({@code !=}). Checks if a field’s value does **not**
     * match the provided value.
     */
    NOT_EQUAL,

    /**
     * Greater‑than operator ({@code >}). Useful for numeric or comparable
     * fields where a higher value is required.
     */
    GREATER_THAN,

    /**
     * Greater‑than‑or‑equal operator ({@code >=}). Allows values that are equal
     * to or exceed the supplied threshold.
     */
    GREATER_THAN_OR_EQUAL,

    /**
     * Less‑than operator ({@code <}). Used for numeric or comparable fields
     * where a lower value is required.
     */
    LESS_THAN,

    /**
     * Less‑than‑or‑equal operator ({@code <=}). Allows values that are equal to
     * or fall below the supplied threshold.
     */
    LESS_THAN_OR_EQUAL
}