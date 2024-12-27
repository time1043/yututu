package com.oswin902.yututubackend.common;

import com.oswin902.yututubackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应封装类
 *
 * @param <T> 响应数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}

// Way 1
// public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
//    return "Hello, " + name;
// }
//
// Way 2
// public BaseResponse<String> hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
//    return new BaseResponse<>(200, "Hello, " + name);
// }
