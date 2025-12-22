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
package com.myenterprise.rest.v1.entity;

import com.myenterprise.rest.v1.model.Facility.TypeEnum;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity that represents a facility offered by a hotel within the system.
 * <p>
 * This entity is mapped to the {@code facilities} table in the database and stores
 * core facility information such as its unique identifier, facility type, and a
 * short description.
 * </p>
 *
 * <p>
 * Instances of this class are managed by the JPA persistence context and are
 * typically used in the data access layer of the application.
 * </p>
 *
 */
@Entity
@Table(name = "facilities")
public class FacilityEntity {

    /**
     * Unique identifier of the facility.
     * <p>
     * Acts as the primary key of the {@code facilities} table and is automatically
     * generated using a UUID strategy.
     * </p>
     */
    @Id
    @GeneratedValue
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelsEntity hotel;

    /**
     * Type of the facility.
     * <p>
     * Defined by {@link com.myenterprise.rest.v1.model.Facility.TypeEnum} and used
     * to categorize the different facilities offered by a hotel.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    /**
     * Short textual description of the facility.
     * <p>
     * Intended to provide a concise summary suitable for UI listings and
     * lightweight API responses.
     * </p>
     */
    private String shortDescription;

    /**
     * Returns the facility type.
     *
     * @return the {@link TypeEnum} representing the facility type
     */
    public TypeEnum getType() {
        return type;
    }

    /**
     * Sets the facility type.
     *
     * @param type the {@link TypeEnum} to assign to this facility
     */
    public void setType(TypeEnum type) {
        this.type = type;
    }

    /**
     * Returns the short description of the facility.
     *
     * @return a brief textual description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the short description of the facility.
     *
     * @param shortDescription a brief textual description to assign
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Sets the hotel.
     *
     * @param hotel a brief textual description to assign
     */
    public void setHotel(HotelsEntity hotel) {
        this.hotel = hotel;
    }

    /**
     * Retrieves the unique identifier of the facility.
     *
     * @return The facility's ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retrieves the hotel.
     *
     * @return The HotelMapper.
     */
    public HotelsEntity getHotel() {
        return hotel;
    }

    /**
     * Compares this facility with another object for equality.
     * <p>
     * Two {@code FacilityEntity} instances are considered equal if they share the
     * same identifier.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FacilityEntity that = (FacilityEntity) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns a hash code value for the facility.
     * <p>
     * The hash code is computed based on the identifier
     * description, ensuring consistency with {@link #equals(Object)}.
     * </p>
     *
     * @return a hash code value for this entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the facility entity.
     *
     * @return a string containing the facility's field values
     */
    @Override
    public String toString() {
        return "FacilityEntity{" +
                "id=" + id +
                ", type=" + type +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }
}
