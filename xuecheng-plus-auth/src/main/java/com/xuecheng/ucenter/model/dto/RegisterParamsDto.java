package com.xuecheng.ucenter.model.dto;

import lombok.Data;

@Data
public class RegisterParamsDto{
    //手机号
    private String cellphone;

    //请求名称
    private String username;

    //邮箱
    private String email;

    //账号名
    private String nickname;

    //新密码
    private String password;

    //确认密码
    private String confirmpwd;

    //验证码key
    private String checkcodekey;

    //验证码
    private String checkcode;


}
