package com.myenterprise.rest.v1.mapper;

import com.myenterprise.rest.v1.entity.FacilityEntity;
import com.myenterprise.rest.v1.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    Facility toModel(FacilityEntity facilityEntity);

    @Mapping(target = "hotel", ignore = true)
    FacilityEntity toEntity(Facility facility);
}
