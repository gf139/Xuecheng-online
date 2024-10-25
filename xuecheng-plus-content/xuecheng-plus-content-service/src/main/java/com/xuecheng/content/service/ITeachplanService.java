package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
public interface ITeachplanService extends IService<Teachplan> {
    /**
     * @description 查询课程计划树型结构
     * @param courseId  课程id
     * @return List<TeachplanDto>
     * @author Mr.M
     * @date 2022/9/9 11:13
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * @description 保存课程计划
     * @param teachplanDto  课程计划信息
     * @return void
     * @author Mr.M
     * @date 2022/9/9 13:39
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
}
