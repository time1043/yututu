package com.oswin902.yututubackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求对象
 * 需要序列化 Plugin: GeneratedSerialVersionUID
 */
@Data
public class UserLoginRequest implements Serializable {

    // 序列化ID 【alt+insert】
    private static final long serialVersionUID = -8142046106039382504L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
