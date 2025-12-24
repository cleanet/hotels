package com.myenterprise.rest.v1.mapper;

import com.myenterprise.rest.v1.entity.HotelsEntity;
import com.myenterprise.rest.v1.model.Hotel;
import com.myenterprise.rest.v1.model.HotelInput;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = FacilityMapper.class
)
public interface HotelMapper {

    Hotel toModel(HotelsEntity entity);

    List<Hotel> toModel( List<HotelsEntity> hotels);

    HotelsEntity toEntity(HotelInput input);
}
