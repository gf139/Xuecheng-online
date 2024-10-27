package com.xuecheng.content.api;

import com.xuecheng.content.service.ITeachplanService;
import com.xuecheng.model.dto.EditCourseTeacherDto;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.CourseTeacher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
public class TeachplanController {

    @Autowired
    ITeachplanService iTeachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {

        return iTeachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplan) {
        iTeachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{courseId}")
    public void deleteTeachplan(@PathVariable Long courseId) {
        iTeachplanService.deleteTeachplan(courseId);
    }


    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{move}/{courseId}")
    public void orderTeachplan(@PathVariable @Pattern(regexp = "moveup|movedown", message = "Invalid move value") String move
            ,@PathVariable @Min(1) Long courseId) {
        iTeachplanService.orderTeachplan(move,courseId);
    }

    @ApiOperation("查询老师接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeacher(@PathVariable Long courseId) {
        return iTeachplanService.getTeacher(courseId);
    }


    @ApiOperation("添加或修改老师接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher savecourseTeacher(@RequestBody EditCourseTeacherDto editCourseTeacherDto) {
        Long companyId = 1232141425L;
        return iTeachplanService.savecourseTeacher(editCourseTeacherDto,companyId);
    }

    @ApiOperation("删除老师接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deletecourseTeacher(@PathVariable Long courseId,@PathVariable Long teacherId) {
        Long companyId = 1232141425L;
        iTeachplanService.deletecourseTeacher(courseId,teacherId,companyId);
    }

    @ApiOperation("删除课程接口")
    @DeleteMapping("/course/{courseId}")
    public void deletecourseTeacher(@PathVariable Long courseId) {
        Long companyId = 1232141425L;
        iTeachplanService.deletecourse(courseId,companyId);
    }
}
