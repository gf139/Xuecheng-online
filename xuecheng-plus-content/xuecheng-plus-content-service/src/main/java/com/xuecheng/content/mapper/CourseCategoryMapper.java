package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.model.dto.CourseCategoryTreeDto;
import com.xuecheng.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
