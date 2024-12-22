package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.model.dto.*;
import com.xuecheng.model.po.CourseBase;
import com.xuecheng.model.po.CourseCategory;
import com.xuecheng.model.po.CourseMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    /**
     * 课程分页查询管理接口
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //构建查询条件对象(教学机构细粒度授权)
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //机构id
        if(companyId!=null){
            queryWrapper.eq(CourseBase::getCompanyId,companyId);
        }

        //不为空才会进行模糊匹配
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());

        //根据课程状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());

        //根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //分页查询参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();

        //返回数据的类型（接口文档）
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items,total,pageParams.getPageNo(),pageParams.getPageSize());
        return courseBasePageResult;
    }


    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        //向课程营销表保存课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto,courseMarket);
        //必须在后，防止没了
        courseMarket.setId(courseId);
        int i = saveCourseMarket(courseMarket);
        if(i<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }

        //查询课程基本信息及营销信息并返回

        return getCourseBaseInfo(courseId);
    }

    //TODO 有bug，保存课程营销信息,验证是否可以保存
    private int saveCourseMarket(CourseMarket courseMarketNew){
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() ==null || courseMarketNew.getPrice().floatValue()<=0){
                throw new XueChengPlusException("课程的价格不能为空并且必须大于0");
            }
        }
        //根据id从课程营销表查询
        Long id = courseMarketNew.getId();
        CourseMarket courseMarketObj = courseMarketMapper.selectById(id);
        if(courseMarketObj == null){
            int insert = courseMarketMapper.insertCourseMarket(courseMarketNew);
            log.info("插入成功");
            return insert;
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            int i = courseMarketMapper.updateById(courseMarketObj);
            log.info("更新成功");
            return i;
        }
    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        //查询基本表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }

        //营销表
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //先拿到原来id的类对象
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        //判断是否是自己公司的
        if(!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //根据原来类对象进行更新
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新基本表
        int i = courseBaseMapper.updateById(courseBase);
        if(i<=0){
            XueChengPlusException.cast("基本信息表更新失败");
        }

        //更新营销表
        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        saveCourseMarket(courseMarket);

        return this.getCourseBaseInfo(courseId);
    }


}
