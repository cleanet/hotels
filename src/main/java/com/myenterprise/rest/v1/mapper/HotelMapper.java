package com.myenterprise.rest.v1.mapper;

import com.myenterprise.rest.v1.entity.HotelsEntity;
import com.myenterprise.rest.v1.model.Hotel;
import com.myenterprise.rest.v1.model.HotelInput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    Hotel toModel(HotelsEntity entity);

    HotelsEntity toEntity(HotelInput input);
}
