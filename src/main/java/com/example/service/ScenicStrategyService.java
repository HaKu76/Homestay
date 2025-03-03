package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicStrategyQueryDto;
import com.example.pojo.entity.ScenicStrategy;
import com.example.pojo.vo.ScenicStrategyVO;

import java.util.List;

public interface ScenicStrategyService {
    Result<Void> save(ScenicStrategy scenicStrategy);

    Result<Void> update(ScenicStrategy scenicStrategy);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<ScenicStrategyVO>> query(ScenicStrategyQueryDto dto);

    Result<Void> audit(Integer id);
}
