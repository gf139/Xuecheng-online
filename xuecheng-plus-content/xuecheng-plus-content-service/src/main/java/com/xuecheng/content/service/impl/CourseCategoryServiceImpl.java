package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.model.dto.CourseCategoryTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    /**
     *
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        //先将list转为map.filter是条件才会执行
        //转化为key是id名，而值是名+属性（一整条完整信息）
        Map<String,CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).collect(
                Collectors.toMap(key->key.getId(),value->value,(key1, key2) -> key2));

        //最终返回的list
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        //依次遍历节点,排除根节点，然后塞入到每个自身的list
        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            if(item.getParentid().equals(id)){
                categoryTreeDtos.add(item);
            }
            //找到当前节点父节点
            CourseCategoryTreeDto courseCategoryParent = map.get(item.getParentid());
            if(courseCategoryParent!=null){
                if(courseCategoryParent.getChildrenTreeNodes()==null){
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<>());
                }
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}
