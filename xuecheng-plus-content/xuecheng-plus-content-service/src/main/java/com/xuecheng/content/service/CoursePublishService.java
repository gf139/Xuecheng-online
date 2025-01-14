package com.xuecheng.content.service;


import com.xuecheng.model.dto.CoursePreviewDto;
import com.xuecheng.model.po.CoursePublish;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程预览、发布接口
 * @date 2022/9/16 14:59
 */
public interface CoursePublishService {


    /**
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @param courseId 课程id
     * @return void
     * @description 提交审核
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    public void commitAudit(Long companyId, Long courseId);

    public void coursepublish(Long companyId, Long courseId);


    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @param file 静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public void uploadCourseHtml(Long courseId, File file);

    CoursePublish getCoursePublish(Long courseId);

    /**
     * @param courseId
     * @return com.xuecheng.content.model.po.CoursePublish
     * @description 查询缓存中的课程信息
     */
    public CoursePublish getCoursePublishCache(Long courseId);
}