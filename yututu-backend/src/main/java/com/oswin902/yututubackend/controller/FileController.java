package com.oswin902.yututubackend.controller;

import com.oswin902.yututubackend.annotation.AuthCheck;
import com.oswin902.yututubackend.common.BaseResponse;
import com.oswin902.yututubackend.common.ResultUtils;
import com.oswin902.yututubackend.constant.UserConstant;
import com.oswin902.yututubackend.exception.BusinessException;
import com.oswin902.yututubackend.exception.ErrorCode;
import com.oswin902.yututubackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;


    /**
     * 测试文件上传【admin】
     *
     * @param multipartFile 上传文件(form表单接收)
     * @return 返回上传成功后的文件地址
     */
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {

        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);  // 创建临时文件
            multipartFile.transferTo(file);  // 将上传的文件写入临时文件
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);

        } catch (Exception e) {
            log.error("file upload error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (file != null) {
                boolean delete = file.delete();  // 删除临时文件
                if (!delete) log.error("file delete error, filepath = {}", filepath);
            }
        }
    }

    /**
     * 测试文件下载【admin】
     * 实现方式：后端流转发
     *
     * @param filepath 文件路径
     * @param response 响应
     */
    @GetMapping("/test/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void testDownloadFile(String filepath, HttpServletResponse response) throws Exception {

        COSObjectInputStream cosObjectInputStream = null;

        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInputStream = cosObject.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(cosObjectInputStream);  // 转换为字节数组

            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);

            // 写入响应
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();  // 刷新

        } catch (Exception e) {
            log.error("file download error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
        } finally {
            if (cosObjectInputStream != null) cosObjectInputStream.close();
        }
    }

}
