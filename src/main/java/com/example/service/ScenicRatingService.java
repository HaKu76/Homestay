package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;
import com.example.pojo.vo.ScenicRatingVO;

import java.util.List;

/**
 * 景点评分服务
 */
public interface ScenicRatingService {
    Result<Void> save(ScenicRating scenicRating);

    Result<List<ScenicRatingVO>> query(ScenicRatingQueryDto dto);
}
