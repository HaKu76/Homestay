package com.example.service.impl;

import com.example.aop.Protector;
import com.example.mapper.ScenicStrategyMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicStrategyQueryDto;
import com.example.pojo.entity.ScenicStrategy;
import com.example.pojo.vo.ScenicStrategyVO;
import com.example.service.ScenicStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点攻略的业务逻辑实现类
 */
@Service
public class ScenicStrategyServiceImpl implements ScenicStrategyService {

    @Resource
    private ScenicStrategyMapper scenicStrategyMapper;

    /**
     * 景点新增
     *
     * @param scenicStrategy 景点实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ScenicStrategy scenicStrategy) {
        //设置创建时间
        scenicStrategy.setCreateTime(LocalDateTime.now());
        // 设置初始审核状态，默认为未审核
        scenicStrategy.setIsAudit(false);
        scenicStrategyMapper.save(scenicStrategy);
        return ApiResult.success();
    }

    /**
     * 景点攻略修改
     *
     * @param scenicStrategy 景点实体
     * @return
     */
    @Override
    public Result<Void> update(ScenicStrategy scenicStrategy) {
        scenicStrategyMapper.update(scenicStrategy);
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
        scenicStrategyMapper.batchDelete(ids);
        return ApiResult.success();
    }


    /**
     * 景点攻略查询
     *
     * @param dto 查询实体
     * @return Result<ScenicStrategyVO>
     */
    @Override
    public Result<List<ScenicStrategyVO>> query(ScenicStrategyQueryDto dto) {
        Integer totalCount = scenicStrategyMapper.queryCount(dto);
        List<ScenicStrategyVO> result = scenicStrategyMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
    /**
     * 景点攻略审核
     *
     * @param id 景点ID
     * @return Result<Void>
     */
    @Override
    public Result<Void> audit(Integer id) {
        ScenicStrategy scenicStrategy = new ScenicStrategy();
        scenicStrategy.setId(id);
        scenicStrategy.setIsAudit(true);
        scenicStrategyMapper.update(scenicStrategy);
        return ApiResult.success("景点攻略审核成功");
    }
}
