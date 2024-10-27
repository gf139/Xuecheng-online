package com.xuecheng.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "EditCourseTeacherDto", description = "修改课程老师基本信息")
public class EditCourseTeacherDto {

    @ApiModelProperty(value = "教师id")
    private Long id;
    /**
     * 课程标识
     */
    @NotEmpty(message = "课程id不能为空")
    @ApiModelProperty(value = "课程名称")
    private Long courseId;

    /**
     * 教师标识
     */
    @ApiModelProperty(value = "教师标识")
    private String teacherName;

    /**
     * 教师职位
     */
    @ApiModelProperty(value = "教师职位")
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "教师简介")
    private String introduction;

    /**
     * 照片
     */
    private String photograph;

}
