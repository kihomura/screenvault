package com.kihomura.screenvault.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kihomura.screenvault.pojo.ReviewDetail;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class ReviewConverter implements AttributeConverter<List<ReviewDetail>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ReviewDetail> reviews) {
        try {
            return objectMapper.writeValueAsString(reviews);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<ReviewDetail> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ReviewDetail.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to list", e);
        }
    }
}
