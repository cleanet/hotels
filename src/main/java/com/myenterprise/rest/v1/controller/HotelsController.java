/*
 *   MIT License
 *
 *  Copyright (c) 2025 cleanet
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
package com.myenterprise.rest.v1.controller;

import com.myenterprise.rest.v1.api.HotelsApiDelegate;
import com.myenterprise.rest.v1.model.Hotel;
import com.myenterprise.rest.v1.model.HotelInput;
import com.myenterprise.rest.v1.service.HotelsService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/**
 * Controller class that implements the {@link HotelsApiDelegate} interface.
 * <p>
 * This class handles all hotel-related operations, acting as the entry point
 * for API requests. It delegates the business logic to the {@link HotelsService}.
 * It is annotated with {@code @Controller} to be a Spring MVC controller and
 * {@code @Transactional} to manage database transactions for its methods.
 * </p>
 */
@Controller
@Transactional
public class HotelsController implements HotelsApiDelegate {

    /**
     * The service layer component that contains the core business logic for hotel operations.
     */
    private final HotelsService hotelsService;

    /**
     * Constructs a new {@code HotelsController} with an injected {@code HotelsService}.
     *
     * @param hotelsService The service responsible for hotel business logic.
     */
    @Autowired
    public HotelsController(HotelsService hotelsService){
        this.hotelsService = hotelsService;
    }

    /**
     * Retrieves a list of all available hotels.
     *
     * @return A {@link ResponseEntity} containing a list of {@link Hotel} objects
     * and an appropriate HTTP status.
     */
    @Override
    public ResponseEntity<List<Hotel>> getHotels() {
        return hotelsService.findAll();
    }

    /**
     * Deletes a specific hotel by its UUID.
     *
     * @param id The unique identifier of the hotel to delete.
     * @return A {@link ResponseEntity} with an HTTP 204 (No Content) status if the
     * deletion is successful.
     */
    @Override
    public ResponseEntity<Void> deleteHotel(UUID id) {
        return hotelsService.remove(id);
    }

    /**
     * Retrieves a specific hotel by its ID.
     *
     * @param id The unique identifier of the hotel to retrieve.
     * @return A {@link ResponseEntity} containing the requested {@link Hotel}
     * if found, or an HTTP 404 (Not Found) status if it does not exist.
     */
    @Override
    public ResponseEntity<Hotel> getHotel(UUID id) {
        return hotelsService.find(id);
    }

    /**
     * Updates the information of an existing hotel identified by its ID.
     *
     * @param id The unique identifier of the hotel to update.
     * @param hotelInput The {@link HotelInput} object containing the updated hotel data.
     * @return A {@link ResponseEntity} containing the updated {@link Hotel} if the
     * operation is successful, or an appropriate HTTP status in case of an error.
     */
    @Override
    public ResponseEntity<Hotel> updateHotel(UUID id,
                                             HotelInput hotelInput) {
        return hotelsService.update(id, hotelInput);
    }

    /**
     * Creates a new hotel from the provided data.
     *
     * @param hotelInput The {@link HotelInput} object containing the data for the
     * new hotel to create.
     * @return A {@link ResponseEntity} containing the created {@link Hotel} and
     * an HTTP 201 (Created) status if the operation is successful.
     */
    @Override
    public ResponseEntity<Hotel> createHotel(@NotNull HotelInput hotelInput) {
        return hotelsService.save(hotelInput);
    }
}