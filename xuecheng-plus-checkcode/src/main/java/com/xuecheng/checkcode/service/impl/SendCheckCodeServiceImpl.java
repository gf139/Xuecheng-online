package com.xuecheng.checkcode.service.impl;

import com.xuecheng.base.utils.PhoneUtil;
import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.CheckCodeService;
import com.xuecheng.checkcode.service.SendCheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * todo 发送验证码
 */
@Service("SendCheckCodeService")
public class SendCheckCodeServiceImpl implements SendCheckCodeService {

    @Resource(name = "NumCheckCodeService")
    private CheckCodeService numcheckCodeService;

    @Override
    public CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto) {

        //获取参数一，手机号或邮箱
        String param1 = checkCodeParamsDto.getParam1();
        //数字生成器service生成验证码
        CheckCodeResultDto generate = numcheckCodeService.generate(checkCodeParamsDto);
        //给手机或邮箱发送验证码
        PhoneUtil phoneUtil = new PhoneUtil();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if(phoneUtil.isMatches(param1)){
            System.out.println("给手机号码为:"+param1+"发送验证码："+generate.getAliasing());
        } else if (param1.matches(emailRegex)) {
            System.out.println("给邮箱为:"+param1+"发送验证码："+generate.getAliasing());
        }
        //发送验证码
        return generate;
    }

}
