package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.model.dto.AddCourseDto;
import com.xuecheng.model.dto.CourseBaseInfoDto;
import com.xuecheng.model.dto.EditCourseDto;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.po.CourseBase;

/**
 * 课程信息管理接口
 */
public interface CourseBaseInfoService {

    //课程分页查询
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据用户id查找用户基本信息
     * @param courseId
     * @return
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 根据id进行更新
     * @param companyId
     * @param editCourseDto
     * @return
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

}
