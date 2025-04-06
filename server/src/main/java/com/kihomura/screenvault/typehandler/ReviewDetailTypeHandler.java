package com.kihomura.screenvault.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kihomura.screenvault.pojo.ReviewDetail;

import java.util.List;

public class ReviewDetailTypeHandler extends AbstractJsonTypeHandler<List<ReviewDetail>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected List<ReviewDetail> parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<ReviewDetail>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON to List<ReviewDetail>", e);
        }
    }

    @Override
    protected String toJson(List<ReviewDetail> obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error converting List<ReviewDetail> to JSON", e);
        }
    }
}