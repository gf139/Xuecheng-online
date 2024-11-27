package com.xuecheng.ucenter.model.dto;

import lombok.Data;

@Data
public class FindPasswordParamsDto {
    //手机号
    private String cellphone;

    //邮箱
    private String email;

    //验证码key
    private String checkcodekey;

    //验证码
    private String checkcode;

    //确认密码
    private String confirmpwd;

    //新密码
    private String password;

}
