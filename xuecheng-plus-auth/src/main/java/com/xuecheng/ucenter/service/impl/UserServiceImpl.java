package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;


    /**
     * 查询用户信息
     * @param s 认证参数AuthParamsDto类型的json数据
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}",s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        //认证方法
        String authType = authParamsDto.getAuthType();
        //authservice获得指定类
        AuthService authService =  applicationContext.getBean(authType + "_authservice",AuthService.class);
        //开始认证(走不同认证逻辑)
        XcUserExt user = authService.execute(authParamsDto);

        return getUserPrincipal(user);


    }

    /**
     * 查询用户信息
     * @param user
     * @return
     */
    public UserDetails getUserPrincipal(XcUserExt user){
        //取出数据库存储的正确密码
        String password  =user.getPassword();
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities= {"test"};
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象,权限信息待实现授权功能时再向UserDetail中加入
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        return userDetails;
    }
}
