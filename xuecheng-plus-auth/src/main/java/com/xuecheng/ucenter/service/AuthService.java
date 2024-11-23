package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @description 认证service
 * @version 1.0
 */
public interface AuthService {
    /**
     * @description 认证方法
     * @param authParamsDto 认证参数
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
