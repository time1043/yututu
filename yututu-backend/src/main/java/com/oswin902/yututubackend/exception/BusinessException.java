package com.oswin902.yututubackend.exception;

import lombok.Getter;

/**
 * 异常类自定义
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public static void main(String[] args) {

    }
}

// Way 1
// if (condition) {
//    throw new RuntimeException("error message");
// }
//
// Way 2
// if (condition) {
//    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "error message");
// }
