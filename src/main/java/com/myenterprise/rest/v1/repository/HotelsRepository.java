package com.myenterprise.rest.v1.repository;

import com.myenterprise.rest.v1.entity.HotelsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing {@link HotelsEntity} instances.
 * <p>
 * This interface extends {@link CrudRepository} to provide basic CRUD (Create, Read, Update, Delete)
 * operations for the {@link HotelsEntity} class. The primary key for this entity is of type {@link UUID}.
 * Spring Data JPA automatically provides an implementation of this interface at runtime.
 * </p>
 */
@Repository
public interface HotelsRepository extends CrudRepository<HotelsEntity, UUID> {
}