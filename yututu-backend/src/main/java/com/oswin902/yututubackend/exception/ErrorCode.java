package com.oswin902.yututubackend.exception;

import lombok.Getter;

/**
 * 异常状态枚举
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "ok"),
    // http 400: bad request; 401: unauthorized; 403: forbidden; 404: not found; 【客户端错误】
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    // http 500: internal server error 【服务器错误】
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
