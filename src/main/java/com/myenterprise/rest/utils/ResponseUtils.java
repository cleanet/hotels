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
package com.myenterprise.rest.utils;

import com.myenterprise.rest.v1.model.Error;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Utility class for creating standardized HTTP responses.
 * This class provides static methods to generate common response types,
 * such as error responses for "not found" or internal server errors.
 */
@Component
public class ResponseUtils {

    /**
     * Autowired constructor.
     * This is an empty constructor that exists to allow Spring to inject this component.
     * It is made private to prevent direct instantiation.
     */
    @Autowired
    private ResponseUtils(){}

    /**
     * Creates a standardized 404 Not Found response entity.
     * This method is specifically designed for cases where a requested hotel is not found.
     *
     * @param <T> The type of the response body.
     * @return A {@link ResponseEntity} with an error body and {@link HttpStatus#NOT_FOUND}.
     */
    @NotNull
    public static <T> ResponseEntity<T> notFoundResponse(){
        Error responseError = new Error();
        responseError.setError("HOTELS-ERROR-00404");
        responseError.setMessage("Hotel not found");
        return ResponseUtils.errorResponse(responseError, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a standardized 500 Internal Server Error response entity.
     * This method is intended for unexpected errors on the server side.
     *
     * @param <T> The type of the response body.
     * @return A {@link ResponseEntity} with an error body and {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     */
    @NotNull
    public static <T> ResponseEntity<T> internalErrorResponse(){
        Error responseError = new Error();
        responseError.setError("HOTELS-ERROR-00500");
        responseError.setMessage("Unexpected error");
        return ResponseUtils.errorResponse(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a generic error response entity with a given error body and HTTP status.
     * This is a versatile method for creating custom error responses.
     *
     * @param errorBody The object to be used as the body of the error response.
     * @param status The HTTP status to be set for the response.
     * @param <T> The type of the response body.
     * @return A {@link ResponseEntity} with the provided body and status.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<T> errorResponse(Object errorBody, HttpStatus status) {
        return (ResponseEntity<T>) new ResponseEntity<>(errorBody, status);
    }
}