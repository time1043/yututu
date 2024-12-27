package com.oswin902.yututubackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户必须登陆 且进一步划分
 */
@Target(ElementType.METHOD)  // 作用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时注解
public @interface AuthCheck {

    /**
     * 必须具有某个角色
     */
    String mustRole() default "";
    //String[] mustRoles() default {""};  // 数组 满足任一角色即可放行
}
