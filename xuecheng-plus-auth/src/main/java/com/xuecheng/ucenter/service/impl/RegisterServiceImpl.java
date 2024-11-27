package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.feignclient.CheckCodeClient;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.RegisterParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    CheckCodeClient checkCodeClient;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 注册账号
     * @param registerParamsDto
     * @return
     */
    @Transactional
    public RestResponse registerAccount(RegisterParamsDto registerParamsDto) {
        //校验验证码，不一致抛出
        String password = registerParamsDto.getPassword();
        String confirmpwd = registerParamsDto.getConfirmpwd();
        if(!password.equals(confirmpwd)){
            XueChengPlusException.cast("两次密码不一致");
        }
        String checkcodekey = registerParamsDto.getCheckcodekey();
        String checkcode = registerParamsDto.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
            XueChengPlusException.cast("验证码错误");
        }
        //查询是否存在该用户
        String cellphone = registerParamsDto.getCellphone();
        String email = registerParamsDto.getEmail();
        XcUser xcUser = null;
        if(cellphone != null) {
            //根据手机查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        }
        if(xcUser != null){
            XueChengPlusException.cast("邮箱或手机号已注册");
        }
        if(email != null) {
            //根据手机查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        }
        if(xcUser != null){
            XueChengPlusException.cast("邮箱或手机号已注册");
        }
        xcUser = new XcUser();
        String encode = passwordEncoder.encode(password);
        xcUser.setCreateTime(LocalDateTime.now());
        xcUser.setPassword(encode);
        xcUser.setUsername(registerParamsDto.getUsername());
        xcUser.setNickname(registerParamsDto.getNickname());
        xcUser.setEmail(registerParamsDto.getEmail());
        xcUser.setStatus("1");
        xcUser.setUtype("101001");
        xcUser.setName("学生"+ UUID.randomUUID());
        xcUser.setCellphone(cellphone);
        xcUserMapper.insert(xcUser);
        //角色关系表
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setUserId(xcUser.getId());
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);

        return RestResponse.success(true,"注册成功");
    }
}
