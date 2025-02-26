package com.example.mapper;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicCategoryQueryDto;
import com.example.pojo.entity.ScenicCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点分类持久化接口
 */
@Mapper
public interface ScenicCategoryMapper {
    void save(ScenicCategory scenicCategory);
    void update(ScenicCategory scenicCategory);
    void batchDelete(@Param(value = "ids") List<Integer> ids);
    List<ScenicCategory> query(ScenicCategoryQueryDto dto);
    // 分页页数
    Integer queryCount(ScenicCategoryQueryDto dto);
}
