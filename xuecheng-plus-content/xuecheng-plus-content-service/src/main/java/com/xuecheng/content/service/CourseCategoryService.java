package com.xuecheng.content.service;

import com.xuecheng.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * 课程信息管理接口
 */
public interface CourseCategoryService {

    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
