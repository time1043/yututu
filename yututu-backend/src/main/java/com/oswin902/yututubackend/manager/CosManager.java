package com.oswin902.yututubackend.manager;

import com.oswin902.yututubackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 文件上传和下载 【可复用】
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键 (唯一位置)
     * @param file 文件
     * @return PutObjectResult
     */
    public PutObjectResult putObject(String key, File file) {
        // https://cloud.tencent.com/document/product/436/65935#f1b2b774-d9cf-4645-8dea-b09a23045503
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);

        // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
        // https://cloud.tencent.com/document/product/436/55378
        // https://cloud.tencent.com/document/product/436/113308

        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);  // 1 表示返回原图信息

        //// 图片处理规则列表
        //List<PicOperations.Rule> rules = new ArrayList<>();
        //// 1. 图片压缩（转成 webp 格式）
        //String webpKey = FileUtil.mainName(key) + ".webp";
        //PicOperations.Rule compressRule = new PicOperations.Rule();
        //compressRule.setFileId(webpKey);
        //compressRule.setBucket(cosClientConfig.getBucket());
        //compressRule.setRule("imageMogr2/format/webp");
        //rules.add(compressRule);
        //// 2. 缩略图处理，仅对 > 20 KB 的图片生成缩略图
        //if (file.length() > 2 * 1024) {
        //    PicOperations.Rule thumbnailRule = new PicOperations.Rule();
        //    // 拼接缩略图的路径
        //    String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
        //    thumbnailRule.setFileId(thumbnailKey);
        //    thumbnailRule.setBucket(cosClientConfig.getBucket());
        //    // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
        //    thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
        //    rules.add(thumbnailRule);
        //}

        // 构造处理参数
        //picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }
}
