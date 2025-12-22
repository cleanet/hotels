package com.myenterprise.rest.v1.mapper;

import com.myenterprise.rest.v1.entity.FacilityEntity;
import com.myenterprise.rest.v1.model.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    Facility toModel(FacilityEntity facilityEntity);

    FacilityEntity toEntity(Facility facility);
}
