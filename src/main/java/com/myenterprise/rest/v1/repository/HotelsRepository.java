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
package com.myenterprise.rest.v1.repository;

import com.myenterprise.rest.v1.entity.HotelsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data repository for {@link HotelsEntity}.
 *
 * <p>This interface extends several Spring Data interfaces to provide a rich
 * set of data‑access operations:</p>
 *
 * <ul>
 *   <li>{@link JpaRepository} – offers standard CRUD methods plus pagination
 *       and sorting capabilities.</li>
 *   <li>{@link JpaSpecificationExecutor} – enables the construction of dynamic
 *       queries using the {@code Specification} API.</li>
 * </ul>
 *
 * <p>The primary key type for {@link HotelsEntity} is {@link UUID}, which means
 * all generated queries will expect a {@code UUID} value when locating a specific
 * hotel record.</p>
 *
 * <p>Spring Data JPA will automatically generate an implementation of this
 * repository at runtime, so developers can simply inject {@code HotelsRepository}
 * wherever they need to interact with the underlying database.</p>
 *
 */
@Repository
public interface HotelsRepository extends
        JpaRepository<HotelsEntity, UUID>,
        JpaSpecificationExecutor<HotelsEntity> {

    // No additional methods are required at this stage.
    // Custom query methods can be added here later if needed,
    // following Spring Data naming conventions.
}