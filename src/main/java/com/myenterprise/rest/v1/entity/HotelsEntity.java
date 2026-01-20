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
package com.myenterprise.rest.v1.entity;

import com.myenterprise.rest.v1.model.Hotel;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import org.springframework.lang.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a hotel entity that maps to the 'hotels' table in a database.
 * This class extends the {@link Hotel} model and adds persistence annotations.
 * It is used by a JPA provider to perform database operations.
 */
@Entity
@Table(name = "hotels")
public class HotelsEntity {
    /**
     * Compares this HotelsEntity to the specified object. The comparison
     * is based on all fields including id, name, description, address, city,
     * rating, and hasWifi.
     *
     * @param hotel The object to compare with.
     * @return true if the given object represents a HotelsEntity equivalent to this HotelsEntity, false otherwise.
     */
    
    public boolean equals(Object hotel) {
        if (hotel == null || getClass() != hotel.getClass()) return false;
        if (!super.equals(hotel)) return false;
        HotelsEntity that = (HotelsEntity) hotel;
        return
                Objects.equals(id, that.id) &&
                        Objects.equals(name, that.name) &&
                        Objects.equals(description, that.description) &&
                        Objects.equals(address, that.address) &&
                        Objects.equals(city, that.city) &&
                        Objects.equals(rating, that.rating) &&
                        Objects.equals(hasWifi, that.hasWifi);
    }

    /**
     * Returns a hash code for this HotelsEntity.
     * The hash code is computed based on all fields.
     *
     * @return a hash code value for this object.
     */
    
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, address, city, rating, hasWifi);
    }

    /**
     * The unique identifier for the hotel.
     * It is the primary key in the database and is automatically generated.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The name of the hotel.
     */
    private String name;

    /**
     * The description of the hotel.
     * This field can be null.
     */
    private @Nullable String description;

    /**
     * The address of the hotel.
     */
    private String address;

    /**
     * The city where the hotel is located.
     */
    private String city;

    /**
     * The rating of the hotel.
     */
    private Float rating;

    /**
     * Indicates whether the hotel has Wi-Fi.
     */
    private Boolean hasWifi;


    /**
     * Indicates the facilities of hotel
     */
    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FacilityEntity> facilities = new ArrayList<>();

    /**
     * Retrieves the name of the hotel.
     *
     * @return The hotel's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the hotel.
     *
     * @param name The new name for the hotel.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the description of the hotel.
     *
     * @return The hotel's description.
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the hotel.
     *
     * @param description The new description for the hotel.
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Retrieves the address of the hotel.
     *
     * @return The hotel's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the hotel.
     *
     * @param address The new address for the hotel.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves the city where the hotel is located.
     *
     * @return The hotel's city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the hotel is located.
     *
     * @param city The new city for the hotel.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Retrieves the rating of the hotel.
     *
     * @return The hotel's rating.
     */
    public Float getRating() {
        return rating;
    }

    /**
     * Sets the rating of the hotel.
     *
     * @param rating The new rating for the hotel.
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     * Retrieves the Wi-Fi availability status of the hotel.
     *
     * @return The hotel's Wi-Fi availability.
     */
    public Boolean getHasWifi() {
        return hasWifi;
    }

    /**
     * Sets the Wi-Fi availability status of the hotel.
     *
     * @param hasWifi The new Wi-Fi availability status.
     */
    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    /**
     * Retrieves the unique identifier of the hotel.
     *
     * @return The hotel's ID.
     */
    public UUID getId() {
        return id;
    }


    /**
     * Retrieves the facilities list of the hotel.
     *
     * @return The hotel's facilities list.
     */
    
    public List<FacilityEntity> getFacilities() {
        return facilities;
    }


    /**
     * Sets the facilities list of the hotel
     *
     * @param facilities The new facilities list.
     */
    public void setFacilities(List<FacilityEntity> facilities) {
        this.facilities = facilities;
    }
}