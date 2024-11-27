package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;

public interface SendCheckCodeService {
    public CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto);
}
