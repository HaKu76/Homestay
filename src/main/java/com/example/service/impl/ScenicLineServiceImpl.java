package com.example.service.impl;

import com.example.mapper.ScenicLineMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicLineQueryDto;
import com.example.pojo.entity.ScenicLine;
import com.example.pojo.vo.ScenicLineVO;
import com.example.service.ScenicLineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点路线的业务逻辑实现类
 */
@Service
public class ScenicLineServiceImpl implements ScenicLineService {

    @Resource
    private ScenicLineMapper scenicLineMapper;

    /**
     * 景点新增
     *
     * @param scenicLine 景点实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ScenicLine scenicLine) {
        //设置创建时间
        scenicLine.setCreateTime(LocalDateTime.now());
        scenicLineMapper.save(scenicLine);
        return ApiResult.success();
    }

    /**
     * 景点修改
     *
     * @param scenicLine 景点实体
     * @return
     */
    @Override
    public Result<Void> update(ScenicLine scenicLine) {
        scenicLineMapper.update(scenicLine);
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
        scenicLineMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicLineVO>> query(ScenicLineQueryDto dto) {
        Integer totalCount = scenicLineMapper.queryCount(dto);
        List<ScenicLineVO> result = scenicLineMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
