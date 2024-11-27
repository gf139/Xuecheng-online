package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;
import com.xuecheng.ucenter.service.FindPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 找回密码接口
 */
@Slf4j
@RestController
public class FindPasswordController {

    @Autowired
    FindPasswordService findPasswordService;

    @RequestMapping("/findpassword")
    public RestResponse findPassword(@RequestBody FindPasswordParamsDto findPasswordParamsDto) {
        RestResponse passwordByPhone = findPasswordService.findPasswordByPhone(findPasswordParamsDto);
        return passwordByPhone;
    }
}
