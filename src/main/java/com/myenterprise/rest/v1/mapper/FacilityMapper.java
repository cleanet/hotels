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
package com.myenterprise.rest.v1.mapper;

import com.myenterprise.rest.v1.entity.FacilityEntity;
import com.myenterprise.rest.v1.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * <p>MapStruct mapper that converts between the {@link Facility} model used by the
 * REST layer and the {@link FacilityEntity} persistence entity.</p>
 *
 * <p>This interface is annotated with {@code @Mapper(componentModel = "spring")} so that
 * Spring will automatically generate an implementation bean that can be injected
 * wherever required.</p>
 *
 * <p>The generated implementation handles the field‑by‑field copying; any fields
 * that require special handling can be configured with {@link Mapping} annotations,
 * as demonstrated for the {@code hotel} property.</p>
 */
@Mapper(componentModel = "spring")
public interface FacilityMapper {

    /**
     * Convert a {@link FacilityEntity} instance to its corresponding {@link Facility}
     * DTO (Data Transfer Object) used by the API layer.
     *
     * @param facilityEntity the persistence entity to map from; may be {@code null}
     * @return a new {@link Facility} populated with the values from {@code facilityEntity},
     *         or {@code null} if the input was {@code null}
     */
    Facility toModel(FacilityEntity facilityEntity);

    /**
     * Convert a {@link Facility} DTO back to a {@link FacilityEntity} suitable for
     * persistence. The {@code hotel} property on the entity is deliberately ignored
     * because it is either managed elsewhere or could cause a circular reference.
     *
     * @param facility the DTO to map from; may be {@code null}
     * @return a new {@link FacilityEntity} populated with the values from {@code facility},
     *         excluding the {@code hotel} field, or {@code null} if the input was {@code null}
     */
    @Mapping(target = "hotel", ignore = true)
    FacilityEntity toEntity(Facility facility);
}