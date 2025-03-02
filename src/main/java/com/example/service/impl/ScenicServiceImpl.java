package com.example.service.impl;


import com.example.mapper.ScenicMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.service.ScenicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点的业务逻辑实现类
 */
@Service
public class ScenicServiceImpl implements ScenicService {

    @Resource
    private ScenicMapper scenicMapper;

    /**
     * 景点新增
     *
     * @param scenic 景点实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Scenic scenic) {
        //设置创建时间
        scenic.setCreateTime(LocalDateTime.now());
        //设置景点初始状态为已审核
        scenic.setStatus(true);
        scenicMapper.save(scenic);
        return ApiResult.success();
    }

    /**
     * 景点修改
     *
     * @param scenic 景点实体
     * @return
     */
    @Override
    public Result<Void> update(Scenic scenic) {
        scenicMapper.update(scenic);
        return ApiResult.success();
    }

    /**
     * 景点删除
     *
     * @param ids 景点ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicVO>> query(ScenicQueryDto dto) {
        Integer totalCount = scenicMapper.queryCount(dto);
        List<ScenicVO> result = scenicMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
