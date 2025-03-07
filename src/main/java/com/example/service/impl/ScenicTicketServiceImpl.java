package com.example.service.impl;

import com.example.mapper.ScenicTicketMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketQueryDto;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.vo.ScenicTicketVO;
import com.example.service.ScenicTicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点门票的业务逻辑实现类
 */
@Service
public class ScenicTicketServiceImpl implements ScenicTicketService {

    @Resource
    private ScenicTicketMapper scenicTicketMapper;

    /**
     * 景点门票新增
     *
     * @param scenicTicket 景点门票实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ScenicTicket scenicTicket) {
        //设置创建时间
        scenicTicket.setCreateTime(LocalDateTime.now());
        scenicTicketMapper.save(scenicTicket);
        return ApiResult.success();
    }

    /**
     * 景点门票修改
     *
     * @param scenicTicket 景点门票实体
     * @return
     */
    @Override
    public Result<Void> update(ScenicTicket scenicTicket) {
        scenicTicketMapper.update(scenicTicket);
        return ApiResult.success();
    }

    /**
     * 景点门票删除
     *
     * @param ids 景点门票ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicTicketMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点门票查询
     *
     * @param dto 查询景点门票实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicTicketVO>> query(ScenicTicketQueryDto dto) {
        Integer totalCount = scenicTicketMapper.queryCount(dto);
        List<ScenicTicketVO> result = scenicTicketMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
