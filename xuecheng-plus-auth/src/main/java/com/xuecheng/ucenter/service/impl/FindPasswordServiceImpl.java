package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.feignclient.CheckCodeClient;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.FindPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FindPasswordServiceImpl implements FindPasswordService {
    @Autowired
    CheckCodeClient checkCodeClient;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 根据手机号找回密码
     * @param findPasswordParamsDto
     * @return
     */
    public RestResponse findPasswordByPhone(FindPasswordParamsDto findPasswordParamsDto) {
        //校验验证码，不一致抛出
        String password = findPasswordParamsDto.getPassword();
        String confirmpwd = findPasswordParamsDto.getConfirmpwd();
        if(!password.equals(confirmpwd)){
            XueChengPlusException.cast("两次密码不一致");
        }
        String checkcodekey = findPasswordParamsDto.getCheckcodekey();
        String checkcode = findPasswordParamsDto.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
            XueChengPlusException.cast("验证码错误");
        }
        //查询是否存在该用户
        String cellphone = findPasswordParamsDto.getCellphone();
        String email = findPasswordParamsDto.getEmail();
        XcUser xcUser = null;
        if(cellphone != null){
            //根据手机查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        }else{
            //邮箱查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getEmail, email));
        }

        //如果找到则修改账号密码
        if(xcUser == null){
            XueChengPlusException.cast("用户不存在");
        }

        //给密码加密
        String encode = passwordEncoder.encode(password);
        //写入数据库
        xcUser.setPassword(encode);
        xcUser.setUpdateTime(LocalDateTime.now());
        int i = xcUserMapper.updateById(xcUser);
        //构建响应返回结果
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(200);
        restResponse.setMsg("找回成功");
        restResponse.setResult("找回成功");

        return restResponse;
    }
}
