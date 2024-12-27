package com.oswin902.yututubackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oswin902.yututubackend.constant.UserConstant;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.exception.ThrowUtils;
import com.oswin902.yututubackend.mapper.UserMapper;
import com.oswin902.yututubackend.model.dto.user.UserQueryRequest;
import com.oswin902.yututubackend.model.entity.User;
import com.oswin902.yututubackend.model.enums.UserRoleEnum;
import com.oswin902.yututubackend.model.vo.LoginUserVO;
import com.oswin902.yututubackend.model.vo.UserVO;
import com.oswin902.yututubackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oswin902
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-22 20:29:23
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 校验参数 (非空 太短)
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度不能少于4位");
        ThrowUtils.throwIf(userPassword.length() < 6 || checkPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码长度不能少于6位");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");

        // 入库查询 用户账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");

        // 密码加密加盐
        String encryptPassword = getEncryptPassword(userPassword);

        // 入库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(UserConstant.DEFAULT_USERNAME);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        user.setUserAvatar(UserConstant.DEFAULT_AVATAR);

        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");

        // 返回用户id
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP请求 (登录态信息)
     * @return 用户信息(脱敏)
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号错误");
        ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码错误");

        // 密码加密加盐
        String encryptPassword = getEncryptPassword(userPassword);

        // 入库查询 账号密码是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) log.error("user login failed, userAccount cannot match userPassword");
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");

        // 保存用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        // 返回用户信息(脱敏)
        return this.getLoginUserVO(user);
    }

    /**
     * 获取登录用户信息 (不返回前端)
     *
     * @param request HTTP请求
     * @return 登录用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {

        // 非必要不用缓存 (数据不一致)
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(userObj == null || currentUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        // 数据库查询 (不追求极致性能优化)
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.SYSTEM_ERROR, "用户信息获取失败");

        return currentUser;
    }

    /**
     * 用户注销 (清除登录态)
     *
     * @param request HTTP请求
     * @return 登录用户信息
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {

        // 判断是否登陆
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObj == null, ErrorCode.OPERATION_ERROR, "用户未登录");

        // 清除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 密码加密加盐 【工具】
     *
     * @param userPassword 用户输入密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        final String SALT = "RunYouClearBoyAndRememberMe";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取用户信息(脱敏) 【工具】
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) return null;

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取用户信息(脱敏-信息更少) 【工具】
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) return null;

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取用户信息(脱敏-信息更少) 【工具】
     *
     * @param userList 用户信息列表
     * @return 脱敏后的用户信息列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) return new ArrayList<>();

        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取查询条件 【工具】
     *
     * @param userQueryRequest 用户查询请求
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "用户查询请求为空");

        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 精确查询：id、角色
        //queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        // 模糊查询：账号、用户名、简介
        queryWrapper.like(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        // 排序：ascend, descend
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    /**
     * 判断用户是否为管理员 【工具】
     *
     * @param user 用户
     * @return 是否为管理员
     */
    @Override
    public boolean isAdmin(User user) {
        //if (user == null) return false;
        //if (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole())) return false;
        //return true;  // 放行
        return (user != null) && (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole()));
    }
}
