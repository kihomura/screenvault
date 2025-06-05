package com.kihomura.screenvault.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kihomura.screenvault.entity.ReviewDetail;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * MyBatis type handler for converting between List<ReviewDetail> and JSON string.
 * Handles serialization and deserialization of review detail lists for database storage.
 * Configured with Jackson ObjectMapper to support Java 8 time types.
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ReviewDetailTypeHandler extends AbstractJsonTypeHandler<List<ReviewDetail>> {

    private final ObjectMapper objectMapper;

    /**
     * Constructor that initializes the type handler with Jackson ObjectMapper.
     * Configures the mapper to handle Java 8 time types and disable timestamp serialization.
     * 
     * @param type the target type for this handler
     */
    public ReviewDetailTypeHandler(Class<List<ReviewDetail>> type) {
        super(type);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * Parses JSON string to List<ReviewDetail> object.
     * 
     * @param json the JSON string to parse
     * @return the parsed List<ReviewDetail> or null if input is null/empty
     * @throws RuntimeException if parsing fails
     */
    @Override
    public List<ReviewDetail> parse(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<ReviewDetail>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON to List<ReviewDetail>: " + json, e);
        }
    }

    /**
     * Converts List<ReviewDetail> object to JSON string.
     * 
     * @param obj the List<ReviewDetail> to convert
     * @return the JSON string representation or null if input is null
     * @throws RuntimeException if conversion fails
     */
    @Override
    public String toJson(List<ReviewDetail> obj) {
        try {
            if (obj == null) {
                return null;
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error converting List<ReviewDetail> to JSON", e);
        }
    }
}