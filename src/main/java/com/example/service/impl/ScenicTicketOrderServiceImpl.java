package com.example.service.impl;

import com.example.mapper.ScenicTicketOrderMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketOrderQueryDto;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.ScenicTicketOrderVO;
import com.example.service.ScenicTicketOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点门票订单的业务逻辑实现类
 */
@Service
public class ScenicTicketOrderServiceImpl implements ScenicTicketOrderService {

    @Resource
    private ScenicTicketOrderMapper scenicTicketOrderMapper;

    /**
     * 景点门票订单新增
     *
     * @param scenicTicketOrder 景点门票订单实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ScenicTicketOrder scenicTicketOrder) {
        //设置创建时间
        scenicTicketOrder.setCreateTime(LocalDateTime.now());
        // 设置默认支付状态为未支付
        scenicTicketOrder.setPayStatus(false);
        scenicTicketOrderMapper.save(scenicTicketOrder);
        return ApiResult.success();
    }

    /**
     * 景点门票订单修改
     *
     * @param scenicTicketOrder 景点门票订单实体
     * @return
     */
    @Override
    public Result<Void> update(ScenicTicketOrder scenicTicketOrder) {
        scenicTicketOrderMapper.update(scenicTicketOrder);
        return ApiResult.success();
    }

    /**
     * 景点门票订单删除
     *
     * @param ids 景点门票订单ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicTicketOrderMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点门票订单查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicTicketOrderVO>> query(ScenicTicketOrderQueryDto dto) {
        Integer totalCount = scenicTicketOrderMapper.queryCount(dto);
        List<ScenicTicketOrderVO> result = scenicTicketOrderMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
