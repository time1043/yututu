package com.oswin902.yututubackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oswin902.yututubackend.annotation.AuthCheck;
import com.oswin902.yututubackend.common.BaseResponse;
import com.oswin902.yututubackend.common.DeleteRequest;
import com.oswin902.yututubackend.common.ResultUtils;
import com.oswin902.yututubackend.constant.UserConstant;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.exception.ThrowUtils;
import com.oswin902.yututubackend.model.dto.user.*;
import com.oswin902.yututubackend.model.entity.User;
import com.oswin902.yututubackend.model.vo.LoginUserVO;
import com.oswin902.yututubackend.model.vo.UserVO;
import com.oswin902.yututubackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册信息
     * @return 注册成功的用户ID
     */
    @PostMapping("/register")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // admin才允许注册
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");

        // Plugin: GenerateGetterAndSetter
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录信息
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前用户信息
     *
     * @param request HTTP请求
     * @return 当前用户信息(脱敏)
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));  // 脱敏
    }

    /**
     * 用户退出登录
     *
     * @param request HTTP请求
     * @return 成功或失败
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 用户添加 【用户管理 admin】
     *
     * @param userAddRequest 用户添加信息
     * @return 注册成功的用户ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");

        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        String encryptPassword = userService.getEncryptPassword(UserConstant.DEFAULT_PASSWORD);  // 密码加密加盐
        user.setUserPassword(encryptPassword);  // 指定默认密码
        //user.setUserRole(UserConstant.DEFAULT_ROLE); // 用户指定 不用默认

        // 入库
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加用户失败");
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id获取用户 【用户管理 admin】
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "请求参数错误");
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取用户VO(信息更少) 【user】
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));  // 脱敏
    }

    /**
     * 用户删除 【用户管理 admin】
     *
     * @param deleteRequest 删除请求(id)
     * @return 成功或失败
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR, "请求参数错误");

        // 被删除的用户不能是admin
        User user = userService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        ThrowUtils.throwIf(user.getUserRole().equals(UserConstant.ADMIN_ROLE), ErrorCode.PARAMS_ERROR, "无法删除管理员");

        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 用户更新 【用户管理 admin】
     *
     * @param userUpdateRequest 更新请求
     * @return 成功或失败
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null,
                ErrorCode.PARAMS_ERROR, "请求参数错误");

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);

        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新用户失败");
        return ResultUtils.success(true);
    }

    /**
     * 用户列表 【用户管理 admin】
     *
     * @param userQueryRequest 查询请求
     * @return 用户列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数错误");

        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));

        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
