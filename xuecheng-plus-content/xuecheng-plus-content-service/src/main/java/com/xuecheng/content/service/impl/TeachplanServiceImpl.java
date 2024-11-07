package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.service.ITeachplanService;
import com.xuecheng.model.dto.BindTeachplanMediaDto;
import com.xuecheng.model.dto.EditCourseTeacherDto;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
@Service
@Slf4j
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements ITeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    TeachplanWorkMapper teachplanWorkMapper;


    /**
     * 根据课程id查询课程计划
     *
     * @param courseId 课程id
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 新增或修改课程计划
     *
     * @param teachplanDto 课程计划信息
     */
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //获得id
        Long id = teachplanDto.getId();

        //如果有id，修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
            //没有id，新增课程计划
            //获取到同父级别的课程计划数量，然后在后面进行叠加
            int count = getTeachplanCount(teachplanDto.getCourseId(),teachplanDto.getParentid());
            Teachplan teachplan = new Teachplan();

            //对其排序
            teachplan.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.insert(teachplan);
        }

    }

    /**
     * 获取最大的排序号
     * @param courseId
     * @param parentid
     * @return
     */
    private int getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        //当前课程id和父级id相同
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
        //TODO 没有逻辑删除，先采用找最大
/**
 * 当没有逻辑删除才需要执行的操作
 **/
        queryWrapper.select(Teachplan::getOrderby);
        // 执行查询并获取结果
        List<Teachplan> orderByList = teachplanMapper.selectList(queryWrapper);
        // 找到orderby字段值最大的记录
        Optional<Teachplan> maxOrderByRecord = orderByList.stream()
                .max((t1, t2) -> t1.getOrderby().compareTo(t2.getOrderby()));
        // 返回最大值，如果为空则返回0
        return maxOrderByRecord.map(Teachplan::getOrderby).orElse(0);
//        return teachplanMapper.selectCount(queryWrapper);
    }

    /**
     * 删除课程计划
     * @param courseId
     */
    @Transactional
    public void deleteTeachplan(Long courseId) {
        //如果有子节点，则不允许删除
        // 通过id获取到对象
        Teachplan teachplan = teachplanMapper.selectById(courseId);
        if(teachplan==null){
            XueChengPlusException.cast("课程计划不存在或已删除");
        }

        //判断是大章节还是小章节
        if(teachplan.getGrade()==2){
            //小节直接删掉
            //teachplanMapper.deleteTeachplan(courseId);
            teachplanMapper.deleteById(courseId);
            //包括关联的视频,动态查询删除
            LambdaQueryWrapper<TeachplanMedia>  queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getCourseId,courseId);
            int delete = teachplanMediaMapper.delete(queryWrapper);
            if(delete!=0){
                log.info("删除小节视频成功");
            }else{
                log.info("未找到视频或删除失败");
            }
        }else if(teachplan.getGrade()==1){
            //大章节，删除大章节，删除小章节，删除视频
            //查找是否有小节，如果有则删除失败
            int childrenCount = getChildrenCount(courseId);
            //没有小节的话
            if(childrenCount==0){
                //teachplanMapper.deleteTeachplan(courseId);
                teachplanMapper.deleteById(courseId);
            }else{
                XueChengPlusException.cast("该大章节下有小节，不允许删除");
            }
        }
    }

    /**
     * 获取子章节数量
     * @param courseId
     * @return
     */
    private int getChildrenCount(Long courseId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        //当前课程id和父级id相同,并且逻辑未删除
        queryWrapper.eq(Teachplan::getParentid, courseId).eq(Teachplan::getStatus, 1);
        return teachplanMapper.selectCount(queryWrapper);
    }

    /**
     * 课程计划排序
     * @param move
     * @param courseId
     */
    @Transactional
    public void orderTeachplan(String move, Long courseId) {
        Long cg = 0L;
        Teachplan teachplan= teachplanMapper.selectById(courseId);
        if(teachplan == null || teachplan.getStatus() == 0){
            XueChengPlusException.cast("无法移动");
        }
        if("moveup".equals(move)){
            //判断上面是否有元素，有元素则交换位置
            cg = teachplanMapper.findUp(teachplan.getParentid(), Long.valueOf(teachplan.getOrderby())
                    ,teachplan.getCourseId());
        }else{
            //判断上面是否有元素，有元素则交换位置
            cg = teachplanMapper.findDown(teachplan.getParentid(), Long.valueOf(teachplan.getOrderby())
                    ,teachplan.getCourseId());
        }
        if(cg == null|| cg == 0){
            XueChengPlusException.cast("已到达边界，无法再移动");
        }
        changeOrder(teachplan.getId(),cg);
        log.info("移动成功!");
    }

    /**
     * 有上面的课程的话才执行
     * @param nowId
     * @param changeId
     * @return
     */
    @Transactional
    public void changeOrder(Long nowId,Long changeId){
        //交换位置id
        Teachplan teachplan = teachplanMapper.selectById(nowId);
        Teachplan changeplan = teachplanMapper.selectById(changeId);
        Integer orderby = teachplan.getOrderby();
        teachplan.setOrderby(changeplan.getOrderby());
        changeplan.setOrderby(orderby);
        // 更新数据库
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(changeplan);
    }

    /**
     * 获取课程老师
     * @param courseId
     * @return
     */
    public List<CourseTeacher> getTeacher(Long courseId) {
        //将条件封装里面，在对其查找直接拿到list
        return courseTeacherMapper.selectList(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
    }


    /**
     * 添加或修改课程老师
     * @param editCourseTeacherDto
     * @return
     */
    @Transactional
    public CourseTeacher savecourseTeacher(EditCourseTeacherDto editCourseTeacherDto,Long companyId) {
        //需要先查找到对象，在对其进行更新，再返回更新后的对象
        Long id = editCourseTeacherDto.getId();
        //校验是不是本机构老师,只有本机构才能添加或修改
        checkCompanyId(editCourseTeacherDto.getCourseId(), companyId);
        //封装数据
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(editCourseTeacherDto,courseTeacher);
        //如果没有那么就是添加
        if(id == null){
            courseTeacher.setCreateDate(LocalDateTime.now());
            //封装完毕插入数据，并查找返回
            courseTeacherMapper.insert(courseTeacher);
        }else {
            courseTeacherMapper.updateById(courseTeacher);
        }
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    private void checkCompanyId(Long courseId,Long companyId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("机构只能修改自己的教师信息");
        }
    }

    /**
     * 删除老师接口
     * @param courseId
     * @param teacherId
     * @param companyId
     * @return
     */
    @Transactional
    public void deletecourseTeacher(Long courseId, Long teacherId, Long companyId) {
        //检验是否可以删除
        checkCompanyId(courseId, companyId);
        courseTeacherMapper.deleteById(teacherId);
    }

    /**
     * 删除课程
     * @param courseId
     * @param companyId
     */
    @Transactional
    public void deletecourse(Long courseId, Long companyId) {
        checkCompanyId(courseId,companyId);
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if("202002".equals(courseBase.getAuditStatus())){
            try {
                //删除课程基本信息
                courseBaseMapper.deleteById(courseId);
                //删除课程营销信息
                courseMarketMapper.deleteById(courseId);
                //删除课程计划
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getCourseId, courseId);
                teachplanMapper.delete(queryWrapper);
                //删除关联的媒体信息
                LambdaQueryWrapper<TeachplanMedia> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(TeachplanMedia::getCourseId, courseId);
                teachplanMediaMapper.delete(queryWrapper1);
                //删除关联的作业信息
                LambdaQueryWrapper<TeachplanWork> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(TeachplanWork::getCourseId, courseId);
                teachplanWorkMapper.delete(queryWrapper2);
                //删除老师信息
                LambdaQueryWrapper<CourseTeacher> queryWrapper3 = new LambdaQueryWrapper<>();
                queryWrapper3.eq(CourseTeacher::getCourseId, courseId);
                courseTeacherMapper.delete(queryWrapper3);
            }catch (Exception e){
                XueChengPlusException.cast("删除失败,原因是"+e.getMessage());
            }
        }else{
            XueChengPlusException.cast("课程已发布，无法删除");
        }
    }

    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //拿到id查找教学计划
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan == null){
            XueChengPlusException.cast("教学计划不存在");
        }
        //如果有，那就判断是否是第二级
        Integer grade = teachplan.getGrade();
        if(grade != 2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //判断完成，可以插入，查找视频是否上传
        Long courseId = teachplan.getCourseId();
        //先删掉原来的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划和媒资管理
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }
}
