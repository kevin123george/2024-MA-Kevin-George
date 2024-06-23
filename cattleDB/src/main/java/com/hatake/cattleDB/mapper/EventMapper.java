package com.hatake.cattleDB.mapper;

import com.hatake.cattleDB.dtos.EventResponse;
import com.hatake.cattleDB.models.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    // Individual mappings
//    @Mapping(source = "attributes", target = "attributes", qualifiedByName = "objectToString")
    EventEntity eventResponseToEventEntity(EventResponse eventResponse);

//    @Mapping(source = "attributes", target = "attributes", qualifiedByName = "stringToObject")
    EventResponse eventEntityToEventResponse(EventEntity eventEntity);

    // List mappings
    List<EventEntity> eventResponseListToEventEntityList(List<EventResponse> eventResponseList);
    List<EventResponse> eventEntityListToEventResponseList(List<EventEntity> eventEntityList);


    // Default method to convert Object to String using toString()
    default String objectToString(Object object) {
        return object != null ? object.toString() : null;
    }

    // Default method to convert String to Object, using a simple approach or JSON deserialization if needed
    default Object stringToObject(String string) {
        // Assuming the object is serialized in a specific way, or use JSON deserialization if required
        return string;
    }

}


