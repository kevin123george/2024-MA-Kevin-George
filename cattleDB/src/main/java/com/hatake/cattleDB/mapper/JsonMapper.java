package com.hatake.cattleDB.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Named;

public class JsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Named("stringToJson")
    public Object stringToJson(String json) {
        try {
            return json != null ? objectMapper.readValue(json, Object.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Named("jsonToString")
    public String jsonToString(Object object) {
        try {
            return object != null ? objectMapper.writeValueAsString(object) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
