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
 *
 * Code extracted of https://www.baeldung.com/rest-api-search-language-spring-data-specifications
 */
package com.myenterprise.rest.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.jetbrains.annotations.Nullable;

/**
 * {@code RsqlSearchOperation} maps the standard RSQL comparison operators
 * provided by {@link RSQLOperators} to a strongly‑typed enum that can be used
 * throughout the application.
 *
 * <p>Each constant holds the corresponding {@link ComparisonOperator}
 * instance, allowing callers to retrieve the underlying operator when
 * constructing specifications or parsing RSQL expressions.</p>
 *
 * <p>The enum also supplies a helper method {@link #getSimpleOperator(ComparisonOperator)}
 * which returns the matching {@code RsqlSearchOperation} for a given
 * {@link ComparisonOperator}, or {@code null} if the operator does not map
 * to any of the defined constants.</p>
 *
 * <p>This class is deliberately immutable – the {@code operator} field is
 * final and set via the constructor.</p>
 */
public enum RsqlSearchOperation {

    /** Equality operator – represented by “==”. */
    EQUAL(RSQLOperators.EQUAL),

    /** Inequality operator – represented by “!=”. */
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),

    /** Greater‑than operator – represented by “>”. */
    GREATER_THAN(RSQLOperators.GREATER_THAN),

    /** Greater‑than‑or‑equal operator – represented by “>=”. */
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),

    /** Less‑than operator – represented by “<”. */
    LESS_THAN(RSQLOperators.LESS_THAN),

    /** Less‑than‑or‑equal operator – represented by “<=”. */
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),

    /** Inclusion operator – represented by “=in=”. */
    IN(RSQLOperators.IN),

    /** Exclusion operator – represented by “=out=”. */
    NOT_IN(RSQLOperators.NOT_IN);

    /** The underlying {@link ComparisonOperator} associated with the enum constant. */
    private final ComparisonOperator operator;

    /**
     * Constructs an {@code RsqlSearchOperation} with the supplied
     * {@link ComparisonOperator}.
     *
     * @param operator the RSQL comparison operator to associate with this enum value
     */
    RsqlSearchOperation(ComparisonOperator operator) {
        this.operator = operator;
    }

    /**
     * Retrieves the {@code RsqlSearchOperation} that corresponds to the given
     * {@link ComparisonOperator}. If the supplied operator does not match any
     * of the enum constants, {@code null} is returned.
     *
     * @param operator the RSQL comparison operator to look up
     * @return the matching {@code RsqlSearchOperation}, or {@code null} if none matches
     */
    @Nullable
    public static RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
        for (RsqlSearchOperation operation : values()) {
            if (operation.getOperator() == operator) {
                return operation;
            }
        }
        return null;
    }

    /**
     * Returns the underlying {@link ComparisonOperator} associated with this
     * enum constant.
     *
     * @return the wrapped {@code ComparisonOperator}
     */
    public ComparisonOperator getOperator() {
        return operator;
    }
}