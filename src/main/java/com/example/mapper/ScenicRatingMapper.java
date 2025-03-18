package com.example.mapper;


import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;
import com.example.pojo.vo.ScenicRatingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 景点评分持久化接口
 */
@Mapper
public interface ScenicRatingMapper {
    void save(ScenicRating scenicRating);

    List<ScenicRatingVO> query(ScenicRatingQueryDto dto);

    // 分页页数
    Integer queryCount(ScenicRatingQueryDto dto);
}
