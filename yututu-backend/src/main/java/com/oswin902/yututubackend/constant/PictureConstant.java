package com.oswin902.yututubackend.constant;

import java.util.Arrays;
import java.util.List;

public interface PictureConstant {

    /**
     * 文件大小 1M
     */
    Long ONE_M = 1024 * 1024L;  // 1m=1024k 1k=1024b

    /**
     * 允许上传的文件后缀列表
     */
    List<String> ALLOWED_FORMAT_LIST = Arrays.asList("jpg", "png", "jpeg", "gif", "bmp", "webp");
}
