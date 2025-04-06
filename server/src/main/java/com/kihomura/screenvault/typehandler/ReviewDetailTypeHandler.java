package com.kihomura.screenvault.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kihomura.screenvault.pojo.ReviewDetail;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)  // 或者JdbcType.JSON，取决于你的数据库字段类型
public class ReviewDetailTypeHandler extends AbstractJsonTypeHandler<List<ReviewDetail>> {

    private final ObjectMapper objectMapper;

    public ReviewDetailTypeHandler() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    protected List<ReviewDetail> parse(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<ReviewDetail>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON to List<ReviewDetail>: " + json, e);
        }
    }

    @Override
    protected String toJson(List<ReviewDetail> obj) {
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