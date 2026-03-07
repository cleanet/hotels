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

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

/**
 * Builder utility that converts an RSQL abstract syntax tree ({@link Node}) into a
 * JPA {@link Specification} that can be used with Spring Data repositories.
 *
 * <p>The class is generic so it can be reused for any entity type {@code T}.
 * It supports both logical nodes (AND / OR) and comparison nodes (e.g.
 * {@code ==}, {@code >}, {@code =in=}, etc.).</p>
 *
 * <p>Typical usage pattern:</p>
 *
 * <pre>
 * Node rsqlRoot = new RSQLParser().parse(rsqlString);
 * Specification&lt;MyEntity&gt; spec = new GenericRsqlSpecBuilder&lt;MyEntity&gt;().createSpecification(rsqlRoot);
 * repository.findAll(spec);
 * </pre>
 *
 * @param <T> the type of the entity for which the {@link Specification} is built
 */
public class GenericRsqlSpecBuilder<T> {

    /**
     * Entry point that determines the concrete type of the supplied {@link Node}
     * and delegates to the appropriate overload.
     *
     * @param node the root of the RSQL AST; may be a {@link LogicalNode},
     *             a {@link ComparisonNode}, or another implementation of {@link Node}
     * @return a {@link Specification} representing the query expressed by the node,
     *         or {@code null} if the node type is unsupported
     */
    public Specification<T> createSpecification(Node node) {
        if (node instanceof LogicalNode logicalNode) {
            return createSpecification(logicalNode);
        }
        if (node instanceof ComparisonNode logicalNode) {
            return createSpecification(logicalNode);
        }
        // Unsupported node type – callers should handle a null return value.
        return null;
    }

    /**
     * Builds a {@link Specification} from a {@link LogicalNode}, which represents a
     * logical combination (AND / OR) of child specifications.
     *
     * <p>The method recursively creates specifications for each child node,
     * then combines them using the logical operator defined on the node.</p>
     *
     * @param logicalNode the logical node to convert; must not be {@code null}
     * @return a composite {@link Specification} that reflects the logical
     *         relationship of the child specifications
     */
    public Specification<T> createSpecification(@NotNull LogicalNode logicalNode) {
        // Convert each child node into a Specification, discarding any null results.
        List<Specification<T>> specs = logicalNode.getChildren()
                .stream()
                .map(this::createSpecification)
                .filter(Objects::nonNull)
                .toList();

        // Initialise the result with the first specification (there is always at least one child).
        Specification<T> result = specs.get(0);

        // Combine the remaining specifications according to the logical operator.
        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (int i = 1; i < specs.size(); i++) {
                result = result.and(specs.get(i));
            }
        } else { // OR
            for (int i = 1; i < specs.size(); i++) {
                result = result.or(specs.get(i));
            }
        }
        return result;
    }

    /**
     * Creates a {@link Specification} from a {@link ComparisonNode}. The
     * {@link GenericRsqlSpecification} encapsulates the selector (field name),
     * the comparison operator (e.g. {@code ==}, {@code >}), and the arguments
     * supplied in the RSQL expression.
     *
     * @param comparisonNode the comparison node to translate; must not be {@code null}
     * @return a {@link Specification} that evaluates the comparison described by the node
     */
    public Specification<T> createSpecification(@NotNull ComparisonNode comparisonNode) {
        return new GenericRsqlSpecification<>(
                comparisonNode.getSelector(),
                comparisonNode.getOperator(),
                comparisonNode.getArguments()
        );
    }
}