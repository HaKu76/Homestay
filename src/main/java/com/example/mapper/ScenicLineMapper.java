package com.example.mapper;


import com.example.pojo.dto.query.extend.ScenicLineQueryDto;
import com.example.pojo.entity.ScenicLine;
import com.example.pojo.vo.ScenicLineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点路线持久化接口
 */
@Mapper
public interface ScenicLineMapper {
    void save(ScenicLine scenicLine);

    void update(ScenicLine scenicLine);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<ScenicLineVO> query(ScenicLineQueryDto dto);

    // 分页页数
    Integer queryCount(ScenicLineQueryDto dto);
}
