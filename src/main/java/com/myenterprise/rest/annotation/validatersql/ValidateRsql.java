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

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import cz.jirutka.rsql.parser.ast.LogicalOperator;

import java.lang.annotation.*;

/**
 * Annotation used to validate an RSQL expression supplied to a controller method
 * parameter. It can be placed on method parameters (e.g. {@code @RequestParam})
 * that accept a string representing an RSQL query.
 *
 * <p>The validation performed by this annotation is purely declarative – it
 * supplies configuration values that a separate validator component will read
 * and enforce at runtime. The annotation itself does not contain any logic.</p>
 *
 * <p>Typical usage:</p>
 *
 * <pre>{@code
 * public ResponseEntity<?> getItems(
 *     @ValidateRsql(depth = 3, maxOperators = 10,
 *                   allowOperators = {Operators.EQUAL, Operators.GREATER_THAN},
 *                   allowComparators = {LogicalOperator.AND})
 *     @RequestParam("filter") String filter) {
 *     …
 * }
 * }</pre>
 *
 * <p>All attributes have sensible defaults, allowing the annotation to be used
 * without specifying any values.</p>
 */
@Retention(RetentionPolicy.RUNTIME)               // Keep the annotation at runtime for reflection.
@Target(ElementType.PARAMETER)                    // Only applicable to method parameters.
@JacksonAnnotationsInside                         // Allows Jackson to treat this as a meta‑annotation.
@Documented                                        // Include this annotation in generated Javadoc.
public @interface ValidateRsql {

    /**
     * Maximum depth of nested logical expressions that the validator will allow.
     * <p>For example, an expression such as {@code (a==1;(b==2;c==3))} has a depth
     * of 2. The default value of {@code 5} is a reasonable compromise between
     * flexibility and protection against overly complex queries.</p>
     *
     * @return the permitted nesting depth.
     */
    int depth() default 5;

    /**
     * Upper bound on the total number of operators that may appear in the RSQL
     * string. This helps prevent excessively large queries that could impact
     * performance.
     *
     * @return the maximum number of operators allowed.
     */
    int maxOperators() default 5;

    /**
     * List of {@link Operators} that are permitted in the query. By default,
     * the most common comparison operators are allowed, including equality,
     * inequality and range checks.
     *
     * @return an array of allowed operator enums.
     */
    Operators[] allowOperators() default {
            Operators.IN,
            Operators.NOT_IN,
            Operators.EQUAL,
            Operators.NOT_EQUAL,
            Operators.GREATER_THAN,
            Operators.GREATER_THAN_OR_EQUAL,
            Operators.LESS_THAN,
            Operators.LESS_THAN_OR_EQUAL
    };

    /**
     * Logical comparators (AND / OR) that may be used to combine individual
     * predicates. The default permits both standard logical operators.
     *
     * @return an array of allowed {@link LogicalOperator}s.
     */
    LogicalOperator[] allowComparators() default {
            LogicalOperator.AND,
            LogicalOperator.OR
    };

    /**
     * Whether the validator should also check that the fields referenced in
     * the RSQL expression belong to a predefined whitelist.
     *
     * <p>If set to {@code true}, the {@code fields()} attribute must contain the
     * list of permitted field names.</p>
     *
     * @return {@code true} to enable field‑whitelisting, {@code false} otherwise.
     */
    boolean validateFields() default false;

    /**
     * Whitelisted field names that may be used in the RSQL query. This attribute
     * is consulted only when {@link #validateFields()} is {@code true}.
     *
     * @return an array of allowed field names.
     */
    String[] fields() default {};
}