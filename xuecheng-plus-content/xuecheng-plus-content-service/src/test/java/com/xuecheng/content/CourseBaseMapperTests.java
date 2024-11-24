package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseMapperTests {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

//    @Test
//    void testCourseBaseMapper() {
//
//        //测试查询接口
//        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
//        //查询条件
//        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
//        queryCourseParamsDto.setCourseName("java");
//        queryCourseParamsDto.setAuditStatus("202004");
//        queryCourseParamsDto.setPublishStatus("203001");
//
//        //分页查询参数
//        PageParams pageParams = new PageParams();
//        pageParams.setPageNo(1L);//页码
//        pageParams.setPageSize(3L);//每页记录数
//        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
//        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
//        System.out.println(courseBasePageResult);
//    }
}
