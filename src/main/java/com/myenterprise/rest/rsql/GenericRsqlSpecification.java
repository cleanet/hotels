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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Specification} that translates a single RSQL comparison
 * (selector + operator + arguments) into a JPA {@link Predicate}.
 *
 * <p>The class is generic so it can be used with any entity type {@code T}.
 * It works together with {@link GenericRsqlSpecBuilder} which builds a tree of
 * these specifications from an entire RSQL AST.</p>
 *
 * @param <T> the entity type for which the specification is created
 */
public class GenericRsqlSpecification<T> implements Specification<T> {

    /** The name of the entity attribute that the comparison targets. */
    private String property;

    /** The RSQL comparison operator (e.g. {@code ==}, {@code >}, {@code =in=}). */
    private ComparisonOperator operator;

    /** The raw argument strings supplied in the RSQL expression. */
    private List<String> arguments;

    /**
     * Constructs the {@link Predicate} that will be applied to a JPA query.
     *
     * <p>The method first casts the raw {@code arguments} to the correct Java
     * type based on the target property's type, then selects the appropriate
     * JPA {@link CriteriaBuilder} operation according to the mapped
     * {@link RsqlSearchOperation}.</p>
     *
     * @param root    the root type in the {@link CriteriaQuery}
     * @param query   the {@link CriteriaQuery} being built (unused here but required by the interface)
     * @param builder the {@link CriteriaBuilder} used to create predicates
     * @return a {@link Predicate} representing the comparison, or {@code null}
     *         if the operator is not recognised (should never happen with valid RSQL)
     */
    @Override
    public Predicate toPredicate(@NotNull Root<T> root,
                                 CriteriaQuery<?> query,
                                 @NotNull CriteriaBuilder builder) {
        // Convert the raw string arguments to the correct Java type(s).
        List<Object> args = castArguments(root);
        Object argument = args.get(0); // Most operators use the first argument only.

        // Determine which simple operation corresponds to the RSQL operator.
        switch (Objects.requireNonNull(RsqlSearchOperation.getSimpleOperator(operator))) {

            case EQUAL: {
                // Equality: treat strings specially to allow wildcard (*) matching.
                if (argument instanceof String) {
                    return builder.like(root.get(property),
                            argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return builder.isNull(root.get(property));
                } else {
                    return builder.equal(root.get(property), argument);
                }
            }
            case NOT_EQUAL: {
                // Inequality: also supports wildcard strings.
                if (argument instanceof String) {
                    return builder.notLike(root.<String>get(property),
                            argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return builder.isNotNull(root.get(property));
                } else {
                    return builder.notEqual(root.get(property), argument);
                }
            }
            case GREATER_THAN: {
                return builder.greaterThan(root.<String>get(property),
                        argument.toString());
            }
            case GREATER_THAN_OR_EQUAL: {
                return builder.greaterThanOrEqualTo(root.<String>get(property),
                        argument.toString());
            }
            case LESS_THAN: {
                return builder.lessThan(root.<String>get(property),
                        argument.toString());
            }
            case LESS_THAN_OR_EQUAL: {
                return builder.lessThanOrEqualTo(root.<String>get(property),
                        argument.toString());
            }
            case IN:
                // “IN” expects a collection of arguments.
                return root.get(property).in(args);
            case NOT_IN:
                return builder.not(root.get(property).in(args));
        }

        // Should not reach here for a valid operator.
        return null;
    }

    /**
     * Casts the raw {@code arguments} (which are strings) to the appropriate
     * Java type based on the property’s declared type.
     *
     * <p>Currently supports {@code Integer} and {@code Long}; everything else
     * is left as a {@code String}.</p>
     *
     * @param root the JPA {@link Root} providing metadata about the entity
     * @return a list of arguments converted to the correct type
     */
    private List<Object> castArguments(@NotNull final Root<T> root) {
        // Determine the Java type of the property we are filtering on.
        Class<?> type = root.get(property).getJavaType();

        // Map each string argument to the appropriate Java object.
        return arguments.stream()
                .map(arg -> {
                    if (type.equals(Integer.class)) {
                        return Integer.parseInt(arg);
                    } else if (type.equals(Long.class)) {
                        return Long.parseLong(arg);
                    } else {
                        return arg; // Keep as String for all other types.
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Primary constructor.
     *
     * @param property   the entity field name to compare
     * @param operator   the RSQL comparison operator
     * @param arguments  the raw argument strings supplied in the RSQL query
     */
    public GenericRsqlSpecification(String property,
                                    ComparisonOperator operator,
                                    List<String> arguments) {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }

    /** @return the property (field) name used in the comparison */
    public String getProperty() {
        return property;
    }

    /** Sets the property (field) name. */
    public void setProperty(String property) {
        this.property = property;
    }

    /** @return the RSQL {@link ComparisonOperator} for this specification */
    public ComparisonOperator getOperator() {
        return operator;
    }

    /** Sets the RSQL operator. */
    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    /** @return the raw argument list supplied in the RSQL expression */
    public List<String> getArguments() {
        return arguments;
    }

    /** Sets the argument list. */
    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}