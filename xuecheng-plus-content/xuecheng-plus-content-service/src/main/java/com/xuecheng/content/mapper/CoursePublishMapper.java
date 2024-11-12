package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.model.po.CoursePublish;
import org.apache.ibatis.annotations.Insert;

/**
 * <p>
 * 课程发布 Mapper 接口
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */

public interface CoursePublishMapper extends BaseMapper<CoursePublish> {

    //新增课程发布信息
    @Insert("INSERT INTO course_publish(id, company_id, name, users, tags, mt, mt_name, st, st_name, grade, teachmode, pic, description, market, teachplan, create_date, status, charge, price, original_price, valid_days) VALUES (#{id}, #{companyId}, #{name}, #{users}, #{tags}, #{mt}, #{mtName}, #{st}, #{stName}, #{grade}, #{teachmode}, #{pic}, #{description}, #{market}, #{teachplan}, #{createDate}, #{status}, #{charge}, #{price}, #{originalPrice}, #{validDays})")
    void insertcourse(CoursePublish coursePublish);
}
