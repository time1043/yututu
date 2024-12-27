package com.oswin902.yututubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oswin902.yututubackend.model.dto.user.UserQueryRequest;
import com.oswin902.yututubackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oswin902.yututubackend.model.vo.LoginUserVO;
import com.oswin902.yututubackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author oswin902
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-22 20:29:23
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP请求 (登录态信息)
     * @return 用户信息(脱敏)
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户信息 (不返回前端)
     *
     * @param request HTTP请求
     * @return 登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销 (清除登录态)
     *
     * @param request HTTP请求
     * @return 登录用户信息
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 密码加密加盐 【工具】
     *
     * @param userPassword 用户输入密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取用户信息(脱敏) 【工具】
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户信息(脱敏-信息更少) 【工具】
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户信息(脱敏-信息更少) 【工具】
     *
     * @param userList 用户信息列表
     * @return 脱敏后的用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     *
     *
     * @param userQueryRequest 用户查询请求
     * @return QueryWrapper
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断用户是否为管理员 【工具】
     *
     * @param user 用户
     * @return 是否为管理员
     */
    boolean isAdmin(User user);
}
