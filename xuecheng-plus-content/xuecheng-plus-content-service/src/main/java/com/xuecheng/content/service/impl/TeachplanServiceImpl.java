package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.service.ITeachplanService;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;
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
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements ITeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

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
}
