package com.example.mapper;

import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商持久化接口
 */
@Mapper
public interface ScenicMapper {
    void save(Scenic scenic);

    void update(Scenic scenic);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<ScenicVO> query(ScenicQueryDto dto);

    // 分页页数
    Integer queryCount(ScenicQueryDto dto);
}
