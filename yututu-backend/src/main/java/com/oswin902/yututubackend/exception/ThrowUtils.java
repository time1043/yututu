package com.oswin902.yututubackend.exception;

/**
 * 异常处理工具类
 */
public class ThrowUtils {

    /**
     * 抛出异常 (类似断言类)
     *
     * @param condition        条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 抛出异常-错误码枚举
     *
     * @param condition 条件
     * @param errorCode 错误码枚举
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 抛出异常-错误码枚举
     *
     * @param condition 条件
     * @param errorCode 错误码枚举
     * @param message   错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
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
//
// Way 3 ✔
// ThrowUtils.throwIf(condition, ErrorCode.FORBIDDEN_ERROR, "error message");
