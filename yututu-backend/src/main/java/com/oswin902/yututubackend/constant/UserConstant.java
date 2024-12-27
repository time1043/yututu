package com.oswin902.yututubackend.constant;

/**
 * 用户模块的常量
 */
public interface UserConstant {

    /**
     * 用户登录态的key
     */
    String USER_LOGIN_STATE = "user_login_state";

    // region 用户信息

    /**
     * 默认头像URL
     */
    String DEFAULT_AVATAR = "https://miro.medium.com/v2/resize:fit:640/format:webp/1*4j2A9niz0eq-mRaCPUffpg.png";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "12345678";  // 5dbf9e5a0566f38faa6a38874c1be06f

    /**
     * 默认用户名
     */
    String DEFAULT_USERNAME = "未名";

    // endregion

    // region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * VIP角色
     */
    String VIP_ROLE = "vip";

    /**
     * SVIP角色
     */
    String SVIP_ROLE = "svip";

    // endregion
}
