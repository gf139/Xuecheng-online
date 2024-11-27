package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.RegisterParamsDto;
import com.xuecheng.ucenter.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册账号
 */
@Slf4j
@RestController
public class RegisterController {
    @Autowired
    RegisterService registerService;

    @RequestMapping("/register")
    public RestResponse registerAccount(@RequestBody RegisterParamsDto registerParamsDto) {
        RestResponse stringRestResponse = registerService.registerAccount(registerParamsDto);
        return stringRestResponse;
    }

}
