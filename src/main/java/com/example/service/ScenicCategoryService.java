package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicCategoryQueryDto;
import com.example.pojo.entity.ScenicCategory;

import java.util.List;

public interface ScenicCategoryService {
    Result<Void> save(ScenicCategory scenicCategory);
    Result<Void> update(ScenicCategory scenicCategory);
    Result<Void> batchDelete(List<Integer> ids);
    Result<List<ScenicCategory>> query(ScenicCategoryQueryDto dto);
}
