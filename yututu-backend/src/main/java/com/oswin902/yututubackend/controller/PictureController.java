package com.oswin902.yututubackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oswin902.yututubackend.annotation.AuthCheck;
import com.oswin902.yututubackend.common.BaseResponse;
import com.oswin902.yututubackend.common.DeleteRequest;
import com.oswin902.yututubackend.common.ResultUtils;
import com.oswin902.yututubackend.constant.UserConstant;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.exception.ThrowUtils;
import com.oswin902.yututubackend.model.dto.picture.*;
import com.oswin902.yututubackend.model.entity.Picture;
import com.oswin902.yututubackend.model.entity.User;
import com.oswin902.yututubackend.model.enums.PictureReviewStatusEnum;
import com.oswin902.yututubackend.model.vo.PictureTagCategory;
import com.oswin902.yututubackend.model.vo.PictureVO;
import com.oswin902.yututubackend.service.PictureService;
import com.oswin902.yututubackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    /**
     * 上传图片 (可重新上传) 【admin】
     *
     * @param multipartFile        上传文件(form表单接收)
     * @param pictureUploadRequest 上传图片请求(自定义上传业务信息)
     * @param request              http请求
     * @return 上传图片结果
     */
    @PostMapping("/upload")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 开放给用户使用 + 管理员审核
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 删除图片 【CRUD】
     *
     * @param deleteRequest 删除请求
     * @param request       http请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 不能用 本人和管理员都能删除图片
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        // 非空校验
        ThrowUtils.throwIf((deleteRequest == null) || (deleteRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);

        // 判断图片是否存在
        Long pictureId = deleteRequest.getId();
        Picture oldPicture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 权限检验
        User loginUser = userService.getLoginUser(request);  // 获取当前用户信息
        ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);  // 仅本人和管理员才能删除图片

        // 操作数据库
        boolean result = pictureService.removeById(pictureId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除图片失败，数据库异常");
        return ResultUtils.success(true);
    }


    /**
     * 更新图片 【CRUD-admin】
     *
     * @param pictureUpdateRequest 更新图片请求
     * @param request              http请求
     * @return 是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {

        ThrowUtils.throwIf((pictureUpdateRequest.getTags() == null) || (pictureUpdateRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);

        // 将实体类和DTO进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));  // 注意将list转为string

        // 数据校验
        pictureService.validPicture(picture);

        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);  // 补充审核参数

        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图片 【CRUD-admin】
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {

        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(picture);
    }

    /**
     * 根据 id 获取图片(脱敏) 【CRUD】
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {

        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(pictureService.getPictureVO(picture, request));  // 脱敏
    }


    /**
     * 分页获取图片列表 【CRUD-admin】
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {

        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表(脱敏) 【CRUD】
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {

        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);  // 限制爬虫
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());  // 普通用户只能看到审核通过的

        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));

        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));  // 脱敏
    }


    /**
     * 编辑图片（给用户使用） 【CRUD】
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {

        ThrowUtils.throwIf((pictureEditRequest == null) || (pictureEditRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);

        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));  // 注意将list转为string
        picture.setEditTime(new Date());  // 设置编辑时间

        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 数据校验
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);  // 仅本人和管理员才能删除图片
        pictureService.fillReviewParams(picture, loginUser);  // 补充审核参数

        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {

        // TODO 前期追求项目快速上线 先完成核心业务流程 后期再优化... (标签表 分类表 计算最优)
        // 不要在前端写死 需要前端请求后端【规范】
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");

        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);

        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 图片审核 【admin】
     *
     * @param pictureReviewRequest 待审核图片请求
     * @param request              http请求
     * @return 审核结果
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {

        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);  // 获取登录用户
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }
}
