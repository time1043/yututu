package com.oswin902.yututubackend.aop;

import com.oswin902.yututubackend.annotation.AuthCheck;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.exception.ThrowUtils;
import com.oswin902.yututubackend.model.entity.User;
import com.oswin902.yututubackend.model.enums.UserRoleEnum;
import com.oswin902.yututubackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点 (对哪些方法进行拦截)
     * @param authCheck 权限校验注解
     * @return 放行或拦截
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);  // 获取登录用户 (未登录拦截)
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());  // 字符串转枚举

        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);  // 字符串转枚举

        // 某功能不需要额外权限 放行
        if (mustRoleEnum == null) return joinPoint.proceed();

        // 某功能需要特定权限 校验权限
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NO_AUTH_ERROR, "用户无权限");
        // 当前功能需要admin权限 && 用户不是admin (vip, svip)
        ThrowUtils.throwIf(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum),
                ErrorCode.NO_AUTH_ERROR, "当前用户不是admin");
        ThrowUtils.throwIf(UserRoleEnum.VIP.equals(mustRoleEnum) && !UserRoleEnum.VIP.equals(userRoleEnum),
                ErrorCode.NO_AUTH_ERROR, "当前用户不是vip");
        ThrowUtils.throwIf(UserRoleEnum.SVIP.equals(mustRoleEnum) && !UserRoleEnum.SVIP.equals(userRoleEnum),
                ErrorCode.NO_AUTH_ERROR, "当前用户不是svip");
        // 最后放行
        return joinPoint.proceed();

    }
}
