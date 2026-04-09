package com.spm.school_project_manage.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功结果
     */

    public static <T> Result<T> success(Object data, Class<T> type) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");

        ObjectMapper objectMapper = JacksonConfig.getObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(data);
            T convertedData = objectMapper.readValue(json, type);
            result.setData(convertedData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Data serialization/deserialization error", e);
        }

        return result;
    }

    /**
     * 重载的 success 方法，专门用于 List 类型数据
     */
    public static <T, V> Result<List<V>> success(List<T> data, Class<V> targetType) {
        Result<List<V>> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");

        ObjectMapper objectMapper = JacksonConfig.getObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(data);

            // 使用 Jackson 进行 List 的转换
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, targetType);
            List<V> convertedData = objectMapper.readValue(json, listType);

            result.setData(convertedData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Data serialization/deserialization error", e);
        }

        return result;
    }

    public static <T, V> Result<PageResult<List<V>>> success(PageResult<List<T>> pageResult, Class<V> targetType) {
        Result<PageResult<List<V>>> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");

        ObjectMapper objectMapper = JacksonConfig.getObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(pageResult.getList());

            // 将 PageResult 内部的 List<T> 转换为 List<V>
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, targetType);
            List<V> convertedData = objectMapper.readValue(json, listType);

            // 创建新的 PageResult，替换转换后的 List<V>
            PageResult<List<V>> newPageResult = (PageResult<List<V>>) new PageResult<>(
                    pageResult.getPageIndex(),
                    pageResult.getPageSize(),
                    pageResult.getTotalRecords(),
                    convertedData
            );

            result.setData(newPageResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Data serialization/deserialization error", e);
        }

        return result;
    }


    public static Result<Integer> success(Integer data) {
        Result<Integer> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static Result<Boolean> success(Boolean data) {
        Result<Boolean> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功结果（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    /**
     * 错误结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 自定义错误结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public T getData() {
        return data;
    }
}
