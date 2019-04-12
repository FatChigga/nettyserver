package com.doyuyu.common;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public enum HttpMethodEnum {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private String value;

    HttpMethodEnum(String value) {
        this.value = value;
    }
}
