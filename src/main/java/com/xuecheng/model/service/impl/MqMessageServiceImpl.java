package com.xuecheng.model.service.impl;

import com.xuecheng.model.domain.po.MqMessage;
import com.xuecheng.model.mapper.MqMessageMapper;
import com.xuecheng.model.service.IMqMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

}
