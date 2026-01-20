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

import com.myenterprise.rest.v1.entity.HotelsEntity;
import com.myenterprise.rest.v1.model.Hotel;
import com.myenterprise.rest.v1.model.HotelInput;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <p>MapStruct mapper responsible for converting between the {@link HotelsEntity}
 * persistence entity and the {@link Hotel} / {@link HotelInput} DTOs used by the
 * REST layer.</p>
 *
 * <p>The mapper is declared with {@code componentModel = "spring"} so that Spring
 * creates an injectable bean for it. It also references {@link FacilityMapper}
 * via the {@code uses} attribute, allowing MapStruct to delegate mapping of any
 * nested {@code Facility} objects contained within a hotel.</p>
 *
 * <p>No custom {@link org.mapstruct.Mapping} annotations are required here because
 * the field names match between source and target types. MapStruct will generate
 * the implementation that copies properties automatically.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = FacilityMapper.class
)
public interface HotelMapper {

    /**
     * Maps a single {@link HotelsEntity} to its corresponding {@link Hotel}
     * DTO.
     *
     * @param entity the persistence entity to map from; may be {@code null}
     * @return a new {@link Hotel} populated with the values from {@code entity},
     *         or {@code null} if the input was {@code null}
     */
    Hotel toModel(HotelsEntity entity);

    /**
     * Maps a list of {@link HotelsEntity} objects to a list of {@link Hotel}
     * DTOs.
     *
     * @param hotels a {@link List} of entities to convert; may be {@code null}
     * @return a {@link List} of {@link Hotel} instances mirroring the order of
     *         the input list, or {@code null} if the input was {@code null}
     */
    List<Hotel> toModel(List<HotelsEntity> hotels);

    /**
     * Converts a {@link HotelInput} DTO (typically received from a client
     * request) into a {@link HotelsEntity} ready for persistence.
     *
     * @param input the incoming DTO containing the data to store; may be {@code null}
     * @return a new {@link HotelsEntity} populated with the values from
     *         {@code input}, or {@code null} if the input was {@code null}
     */
    HotelsEntity toEntity(HotelInput input);
}