package com.xuecheng.model.dto;

import com.xuecheng.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
