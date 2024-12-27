package com.oswin902.yututubackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求对象
 * 需要序列化 Plugin: GeneratedSerialVersionUID
 */
@Data
public class UserRegisterRequest implements Serializable {

    // 序列化ID 【alt+insert】
    private static final long serialVersionUID = -3318155080588640494L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
