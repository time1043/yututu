package com.oswin902.yututubackend.controller;

import com.oswin902.yututubackend.common.BaseResponse;
import com.oswin902.yututubackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     *
     * @return BaseResponse<String>
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("Healthy OK");
    }
}
