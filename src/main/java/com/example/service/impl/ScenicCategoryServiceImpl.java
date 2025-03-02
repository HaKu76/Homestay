package com.example.service.impl;


import com.example.mapper.ScenicCategoryMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicCategoryQueryDto;
import com.example.pojo.entity.ScenicCategory;
import com.example.service.ScenicCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点分类的业务逻辑实现类
 */
@Service
public class ScenicCategoryServiceImpl implements ScenicCategoryService {

    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;

    /**
     * 景点分类新增
     *
     * @param scenicCategory 景点分类实体
     * @return
     */
    @Override
    public Result<Void> save(ScenicCategory scenicCategory) {
        scenicCategoryMapper.save(scenicCategory);
        return ApiResult.success();
    }

    /**
     * 景点分类修改
     *
     * @param scenicCategory 景点分类实体
     * @return
     */
    @Override
    public Result<Void> update(ScenicCategory scenicCategory) {
        scenicCategoryMapper.update(scenicCategory);
        return ApiResult.success();
    }

    /**
     * 景点分类删除
     *
     * @param ids 景点分类ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicCategoryMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点分类查询
     *
     * @param dto 查询实体
     * @return Result<List < ScenicCategory>>
     */
    @Override
    public Result<List<ScenicCategory>> query(ScenicCategoryQueryDto dto) {
        Integer totalCount = scenicCategoryMapper.queryCount(dto);
        List<ScenicCategory> result = scenicCategoryMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
