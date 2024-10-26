package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.service.ITeachplanService;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;
import com.xuecheng.model.po.TeachplanMedia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
/**
 * 当没有逻辑删除才需要执行的操作
        queryWrapper.select(Teachplan::getOrderby);
        // 执行查询并获取结果
        List<Teachplan> orderByList = teachplanMapper.selectList(queryWrapper);
        // 找到orderby字段值最大的记录
        Optional<Teachplan> maxOrderByRecord = orderByList.stream()
                .max((t1, t2) -> t1.getOrderby().compareTo(t2.getOrderby()));
        // 返回最大值，如果为空则返回0
        return maxOrderByRecord.map(Teachplan::getOrderby).orElse(0);
 **/
        return teachplanMapper.selectCount(queryWrapper);
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
        if(teachplan==null || teachplan.getStatus()==0){
            XueChengPlusException.cast("课程计划不存在或已删除");
        }

        //判断是大章节还是小章节
        if(teachplan.getGrade()==2){
            //小节直接删掉
            teachplanMapper.deleteTeachplan(courseId);
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
                teachplanMapper.deleteTeachplan(courseId);
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
}
