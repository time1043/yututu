package com.oswin902.yututubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oswin902.yututubackend.model.dto.picture.PictureEditRequest;
import com.oswin902.yututubackend.model.dto.picture.PictureQueryRequest;
import com.oswin902.yututubackend.model.dto.picture.PictureReviewRequest;
import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
import com.oswin902.yututubackend.model.entity.Picture;
import com.oswin902.yututubackend.model.entity.User;
import com.oswin902.yututubackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huangyingzhu
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2024-12-26 16:06:44
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile        图片
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser            用户 【鉴权】
     * @return 图片信息
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 获取图片信息(脱敏-单条) 【工具】
     *
     * @param picture 未脱敏图片
     * @param request http请求
     * @return 用户信息
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片信息(脱敏-多条|分页) 【工具】
     *
     * @param picturePage 一页图片(未脱敏)
     * @param request     http请求
     * @return 图片脱敏
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     *
     * @param picture 图片
     */
    void validPicture(Picture picture);

    /**
     * 获取查询条件 【工具】
     *
     * @param pictureQueryRequest 查询条件
     * @return QueryWrapper
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片审核 【admin】
     *
     * @param pictureReviewRequest 审核信息
     * @param loginUser            用户鉴权
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数 【工具】
     *
     * @param picture   图片
     * @param loginUser 用户
     */
    void fillReviewParams(Picture picture, User loginUser);
}
