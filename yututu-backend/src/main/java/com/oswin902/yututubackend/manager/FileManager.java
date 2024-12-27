package com.oswin902.yututubackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.oswin902.yututubackend.common.ResultUtils;
import com.oswin902.yututubackend.config.CosClientConfig;
import com.oswin902.yututubackend.constant.PictureConstant;
import com.oswin902.yututubackend.exception.BusinessException;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.exception.ThrowUtils;
import com.oswin902.yututubackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * 文件服务
 */
@Slf4j
@Service  // 偏业务
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param multipartFile    文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传图片结果
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片
        validPicture(multipartFile);

        // 图片上传地址
        String uuid = RandomUtil.randomString(16);  // 文件名称相同 也不意味着是同一个文件
        String originalFilename = multipartFile.getOriginalFilename();  // 原始文件名
        String formatDate = DateUtil.formatDate(new Date());  // 时间戳
        // 自己约定文件上传路径 而不是使用原始文件名称 可增加安全性
        String uploadFilename = String.format("%s_%s.%s", formatDate, uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        // 上传文件
        File file = null;
        try {
            file = File.createTempFile(uploadPath, null);  // 创建临时文件
            multipartFile.transferTo(file);  // 将上传的文件写入临时文件
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);

            // 解析结果并返回
            // https://cloud.tencent.com/product/ci 【需要开数据万象CI服务】
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();  // 图片信息对象
            String format = imageInfo.getFormat();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();  // 计算宽高比
            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicFormat(format);
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            //uploadPictureResult.setThumbnailUrl();
            //uploadPictureResult.setPicColor();
            return uploadPictureResult;

        } catch (Exception e) {
            log.error("图片上传到对象存储失败 ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            // 临时文件清理
            deleteTempFile(file);  // ctrl, alt, m
        }
    }

    /**
     * 校验图片 (若不符合要求 则抛出异常) 【工具】
     *
     * @param multipartFile 文件
     */
    private void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");

        // 校验文件大小
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > 2 * PictureConstant.ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");

        // 校验文件格式
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());  // 文件后缀
        ThrowUtils.throwIf(!PictureConstant.ALLOWED_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式不正确");
    }

    /**
     * 清理临时文件 【工具】
     *
     * @param file 文件
     */
    public static void deleteTempFile(File file) {
        if (file == null) return;

        boolean deleteResult = file.delete();  // 删除临时文件
        if (!deleteResult) log.error("file delete error, filepath = {}", file.getAbsoluteFile());
    }

}
