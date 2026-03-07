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

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.jpa.domain.Specification;

/**
 * {@code CustomRsqlVisitor} is an implementation of {@link RSQLVisitor} that converts
 * RSQL AST nodes into JPA {@link Specification}s.
 *
 * <p>This visitor delegates the heavy lifting to {@link GenericRsqlSpecBuilder},
 * which knows how to build a {@link Specification} from each type of node.
 *
 * <p>The generic type {@code T} represents the entity type for which the
 * specifications will be created.</p>
 *
 * <p>Typical usage is within a Spring Data repository where an RSQL query
 * string is parsed into an AST and then visited by this class to obtain a
 * {@link Specification} that can be passed to {@code findAll(Specification)}.</p>
 *
 * @param <T> the type of the entity the specifications operate on
 */
public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    /** Builder used to create {@link Specification}s from RSQL nodes. */
    private final GenericRsqlSpecBuilder<T> builder;

    /**
     * Constructs a new {@code CustomRsqlVisitor}.
     *
     * <p>The constructor initialises a fresh {@link GenericRsqlSpecBuilder}
     * instance that will be reused for each visitation.</p>
     */
    public CustomRsqlVisitor() {
        builder = new GenericRsqlSpecBuilder<>();
    }

    /**
     * Visits an {@link AndNode} and creates a {@link Specification} representing
     * the logical AND of its child predicates.
     *
     * @param node  the {@code AndNode} to visit
     * @param param an unused parameter required by the {@link RSQLVisitor}
     *              interface (always {@code null})
     * @return a {@link Specification} that combines the child specifications
     *         with logical AND
     */
    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return builder.createSpecification(node);
    }

    /**
     * Visits an {@link OrNode} and creates a {@link Specification} representing
     * the logical OR of its child predicates.
     *
     * @param node  the {@code OrNode} to visit
     * @param param an unused parameter required by the {@link RSQLVisitor}
     *              interface (always {@code null})
     * @return a {@link Specification} that combines the child specifications
     *         with logical OR
     */
    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return builder.createSpecification(node);
    }

    /**
     * Visits a {@link ComparisonNode} and creates a {@link Specification}
     * representing the comparison expressed by the node (e.g. {@code ==},
     * {@code !=}, {@code >}, etc.).
     *
     * @param node   the {@code ComparisonNode} to visit
     * @param params an unused parameter required by the {@link RSQLVisitor}
     *               interface (always {@code null})
     * @return a {@link Specification} that encapsulates the comparison logic
     */
    @Override
    public Specification<T> visit(ComparisonNode node, Void params) {
        return builder.createSpecification(node);
    }
}