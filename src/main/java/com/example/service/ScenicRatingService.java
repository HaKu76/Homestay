package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;

import java.util.List;

/**
 * 景点评分服务
 */
public interface ScenicRatingService {
    Result<Void> save(ScenicRating scenicRating);

    Result<List<ScenicRating>> query(ScenicRatingQueryDto dto);
}
