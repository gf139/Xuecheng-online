package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.model.po.CourseMarket;

/**
 * <p>
 * 课程营销信息 Mapper 接口
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
public interface CourseMarketMapper extends BaseMapper<CourseMarket> {
   int insertCourseMarket(CourseMarket courseMarket);

}
