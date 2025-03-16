package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.SelectedVO;

import java.util.List;

public interface ScenicService {
    Result<Void> save(Scenic scenic);

    Result<Void> update(Scenic scenic);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<ScenicVO>> query(ScenicQueryDto dto);

    Result<List<ScenicVO>> queryVendorScenic(ScenicQueryDto dto);

    Result<List<SelectedVO>> querySelectedScenic();
}
