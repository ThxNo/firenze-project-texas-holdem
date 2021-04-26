package com.thoughtworks.firenze.texas.holdem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CloneUtil {
    private final static ObjectMapper mapper = new ObjectMapper();

    public static <T> T clone(Object obj, Class<T> clazz) {
        try {
            return mapper.readValue(mapper.writeValueAsString(obj), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
