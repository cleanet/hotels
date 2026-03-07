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
package com.myenterprise.rest.v1.service;

import com.myenterprise.rest.rsql.CustomRsqlVisitor;
import com.myenterprise.rest.utils.ResponseUtils;
import com.myenterprise.rest.v1.entity.FacilityEntity;
import com.myenterprise.rest.v1.entity.HotelsEntity;
import com.myenterprise.rest.v1.mapper.FacilityMapper;
import com.myenterprise.rest.v1.mapper.HotelMapper;
import com.myenterprise.rest.v1.model.Hotel;
import com.myenterprise.rest.v1.model.HotelInput;
import com.myenterprise.rest.v1.repository.HotelsRepository;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing hotel-related business logic.
 * <p>
 * This class provides methods for performing CRUD (Create, Read, Update, Delete) operations on hotels.
 * It interacts with the {@link HotelsRepository} for data persistence and uses {@link ResponseUtils}
 * to create standardized HTTP responses.
 * </p>
 */
@Service
public class HotelsService {

    /**
     * Repository for accessing and managing hotel data in the database.
     */
    private final HotelsRepository hotelsRepository;

    private final HotelMapper hotelMapper;

    private final FacilityMapper facilityMapper;

    private static final String HOTEL_NOT_FOUND  = "Hotel not found";
    private static final String ERROR_UNEXPECTED = "error unexpected";

    /**
     * Constructs the {@code HotelsService} with a {@code HotelsRepository} dependency.
     *
     * @param hotelsRepository The repository for hotel data access.
     */
    @Autowired
    public HotelsService(HotelsRepository hotelsRepository, HotelMapper hotelMapper, FacilityMapper facilityMapper){
        this.hotelMapper = hotelMapper;
        this.hotelsRepository = hotelsRepository;
        this.facilityMapper = facilityMapper;
    }

    /**
     * Updates an existing hotel's information.
     *
     * @param id The unique identifier of the hotel to update.
     * @param hotelInput The {@link HotelInput} object containing the updated data.
     * @return A {@link ResponseEntity} with the updated {@link Hotel} and an HTTP 200 (OK) status if successful,
     * or a not found or internal error response if the operation fails.
     */
    public ResponseEntity<Hotel> update( UUID id, HotelInput hotelInput ){
        try{
            if (hotelsRepository.findById(id).isEmpty()) return ResponseUtils.notFoundResponse(HOTEL_NOT_FOUND);
            HotelsEntity hotel = hotelsRepository.findById(id).orElseThrow();
            hotel.setName(hotelInput.getName());
            hotel.setDescription(hotelInput.getDescription());
            hotel.setAddress(hotelInput.getAddress());
            hotel.setCity(hotelInput.getCity());
            hotel.setRating(hotelInput.getRating());
            hotel.setHasWifi(hotelInput.getHasWifi());
            hotel.getFacilities().clear();
            hotelInput.getFacilities().forEach(facility -> {
                FacilityEntity facilityEntity = facilityMapper.toEntity(facility);
                facilityEntity.setHotel(hotel);
                hotel.getFacilities().add(facilityEntity);
            });
            HotelsEntity hotelSaved = hotelsRepository.save(hotel);
            Hotel response = hotelMapper.toModel(hotelSaved);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch ( Exception error ){
            error.printStackTrace();
            return ResponseUtils.internalErrorResponse(ERROR_UNEXPECTED);
        }
    }

    /**
     * Retrieves a hotel by its unique identifier.
     *
     * @param id The UUID of the hotel to retrieve.
     * @return A {@link ResponseEntity} with the found {@link Hotel} and an HTTP 200 (OK) status if found,
     * or a not found or internal error response if the operation fails.
     */
    public ResponseEntity<Hotel> find( UUID id ){
        try{
            if (hotelsRepository.findById(id).isEmpty()) return ResponseUtils.notFoundResponse(HOTEL_NOT_FOUND);
            Hotel hotel = hotelMapper.toModel(hotelsRepository.findById(id).get());
            return new ResponseEntity<>(hotel, HttpStatus.OK);
        } catch ( Exception error ){
            error.printStackTrace();
            return ResponseUtils.internalErrorResponse(ERROR_UNEXPECTED);
        }
    }

    /**
     * Retrieves a collection of all hotels stored in the system.
     *
     * <p>This method optionally accepts an RSQL filter string. If a filter is supplied,
     * it is parsed into a {@link Specification} which is then used to query the
     * {@code hotelsRepository}. When {@code filters} is {@code null}, the method simply
     * returns every {@link HotelsEntity} persisted in the database.</p>
     *
     * <p>The resulting {@link HotelsEntity} objects are converted to the public
     * {@link Hotel} model via {@code hotelMapper} before being wrapped in a
     * {@link ResponseEntity} with an HTTP 200 (OK) status.</p>
     *
     * <p>If any exception occurs during processing (for example, a parsing error
     * or a database failure), the stack trace is printed and a generic internal‑error
     * response is returned using {@link ResponseUtils#internalErrorResponse()}.</p>
     *
     * @param filters an optional RSQL expression used to filter the results; may be
     *                {@code null} to retrieve all records.
     * @return a {@link ResponseEntity} containing a {@link List} of {@link Hotel}
     *         objects and an HTTP 200 status when successful, or an internal‑error
     *         response if an exception is thrown.
     */
    public ResponseEntity<List<Hotel>> findAll(String filters) {
        try {
            List<HotelsEntity> hotels;
            if (filters != null) {
                Node rootNode = new RSQLParser().parse(filters);
                Specification<HotelsEntity> specification =
                        rootNode.accept(new CustomRsqlVisitor<>());
                hotels = hotelsRepository.findAll(specification).stream()
                        .toList();
            } else {
                hotels = hotelsRepository.findAll().stream()
                        .toList();
            }
            return new ResponseEntity<>(hotelMapper.toModel(hotels), HttpStatus.OK);
        } catch (Exception error) {
            error.printStackTrace();
            return ResponseUtils.internalErrorResponse(ERROR_UNEXPECTED);
        }
    }

    /**
     * Removes a hotel from the database by its unique identifier.
     *
     * @param id The UUID of the hotel to remove.
     * @return A {@link ResponseEntity} with an HTTP 204 (No Content) status if the deletion is successful,
     * or a not found or internal error response if the operation fails.
     */
    public ResponseEntity<Void> remove(@NotNull UUID id){
        try{
            if (hotelsRepository.findById(id).isEmpty()) return ResponseUtils.notFoundResponse(HOTEL_NOT_FOUND);
            hotelsRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch ( Exception error ){
            error.printStackTrace();
            return ResponseUtils.internalErrorResponse(ERROR_UNEXPECTED);
        }
    }

    /**
     * Saves a new hotel to the database.
     *
     * @param hotelInput The {@link HotelInput} object containing the data for the new hotel.
     * @return A {@link ResponseEntity} with the newly created {@link Hotel} and an HTTP 201 (Created) status if successful,
     * or an internal error response if the operation fails.
     */
    public ResponseEntity<Hotel> save(@NotNull HotelInput hotelInput){
        try{
            HotelsEntity hotel = hotelMapper.toEntity(hotelInput);
            hotel.getFacilities().forEach(facility -> facility.setHotel(hotel));
            HotelsEntity hotelSaved = hotelsRepository.save(hotel);
            Hotel response = hotelMapper.toModel(hotelSaved);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch ( Exception error ){
            error.printStackTrace();
            return ResponseUtils.internalErrorResponse(ERROR_UNEXPECTED);
        }
    }
}