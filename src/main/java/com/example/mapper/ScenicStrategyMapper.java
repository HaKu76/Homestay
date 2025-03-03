package com.example.mapper;


import com.example.pojo.dto.query.extend.ScenicStrategyQueryDto;
import com.example.pojo.entity.ScenicStrategy;
import com.example.pojo.vo.ScenicStrategyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点攻略持久化接口
 */
@Mapper
public interface ScenicStrategyMapper {
    void save(ScenicStrategy scenicStrategy);

    void update(ScenicStrategy scenicStrategy);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<ScenicStrategyVO> query(ScenicStrategyQueryDto dto);

    // 分页页数
    Integer queryCount(ScenicStrategyQueryDto dto);
}
