package com.xuecheng.content.service.impl;


import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.ITeachplanService;
import com.xuecheng.model.dto.CourseBaseInfoDto;
import com.xuecheng.model.dto.CoursePreviewDto;
import com.xuecheng.model.dto.TeachplanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/16 15:37
 * @version 1.0
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

 @Autowired
 CourseBaseInfoService courseBaseInfoService;

 @Autowired
 ITeachplanService teachplanService;


 @Override
 public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

  //课程基本信息、营销信息
  CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

  //课程计划信息
  List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);

  CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
  coursePreviewDto.setCourseBase(courseBaseInfo);
  coursePreviewDto.setTeachplans(teachplanTree);
  return coursePreviewDto;
 }
}