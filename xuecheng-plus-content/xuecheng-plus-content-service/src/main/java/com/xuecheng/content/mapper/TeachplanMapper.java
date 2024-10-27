package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author goldenfox
 * @since 2024-10-09
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    public List<TeachplanDto> selectTreeNodes(long courseId);

    /**
     * 逻辑删除
     * @param courseId
     */
    public void deleteTeachplan(Long courseId);

    /**
     * 查找上一个元素位置
     * @param parentId
     * @param orderId
     * @return
     */
    public Long findUp(@Param("parentId") Long parentId, @Param("orderId") Long orderId, @Param("courseId") Long courseId);

    /**
     * 查找下一个元素位置
     * @param parentId
     * @param orderId
     * @return
     */
    public Long findDown(@Param("parentId") Long parentId, @Param("orderId") Long orderId, @Param("courseId") Long courseId);
}
