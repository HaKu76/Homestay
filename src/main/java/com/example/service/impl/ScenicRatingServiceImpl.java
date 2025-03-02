package com.example.service.impl;


import com.example.mapper.ScenicRatingMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;
import com.example.service.ScenicRatingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点评分的业务逻辑实现类
 */
@Service
public class ScenicRatingServiceImpl implements ScenicRatingService {

    @Resource
    private ScenicRatingMapper scenicRatingMapper;

    /**
     * 景点新增
     *
     * @param scenicRating 景点实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ScenicRating scenicRating) {
        //设置评分时间
        scenicRating.setCreateTime(LocalDateTime.now());
        scenicRatingMapper.save(scenicRating);
        return ApiResult.success();
    }

    /**
     * 景点查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicRating>> query(ScenicRatingQueryDto dto) {
        Integer totalCount = scenicRatingMapper.queryCount(dto);
        List<ScenicRating> result = scenicRatingMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
