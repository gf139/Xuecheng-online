package com.xuecheng.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.learning.model.po.XcCourseTables;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface XcCourseTablesMapper extends BaseMapper<XcCourseTables> {

//    @Select("SELECT * FROM xc_course_tables WHERE user_id = #{userId} AND course_id = #{courseId}")
//    XcCourseTables selectByIdCourseId(String userId, Long courseId);
}
