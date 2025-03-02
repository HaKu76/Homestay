package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicLineQueryDto;
import com.example.pojo.entity.ScenicLine;
import com.example.pojo.vo.ScenicLineVO;

import java.util.List;

public interface ScenicLineService {
    Result<Void> save(ScenicLine scenicLine);

    Result<Void> update(ScenicLine scenicLine);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<ScenicLineVO>> query(ScenicLineQueryDto dto);
}
