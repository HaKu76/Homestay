package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.ScenicTicketMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.dto.query.extend.ScenicTicketQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.vo.ScenicTicketVO;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.VendorVO;
import com.example.service.ScenicTicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点门票的业务逻辑实现类
 */
@Service
public class ScenicTicketServiceImpl implements ScenicTicketService {

    @Resource
    private ScenicTicketMapper scenicTicketMapper;
    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private VendorMapper vendorMapper;

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
        // 若景点门票状态为不可用设置为false,防止空值
        if (scenicTicket.getUseStatus() == null) {
            scenicTicket.setUseStatus(false);
        }
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

    /**
     * 查询供应商管理的景点门票信息
     *
     * @return Result<Void>
     */
    @Override
    public Result<List<ScenicTicketVO>> queryVendorTicket() {
        // 用户名下的供应商信息ID
        Integer vendorId = getVendorId();
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setVendorId(vendorId);
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);
        // 取出所有景点的ID
        List<Integer> scenicIds = scenicVOS.stream()
                .map(Scenic::getId)
                .collect(Collectors.toList());
        List<ScenicTicketVO> scenicTicketVOS =
                scenicTicketMapper.queryByScenicIds(scenicIds);

        return ApiResult.success(scenicTicketVOS);
    }


    private Integer getVendorId() {
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        // 由用户ID去查回来的供应商信息
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 1. 要么真的没有 2. 有的话，只有一项
        if (vendorVOS.isEmpty()) {
            return null;
        }
        return vendorVOS.get(0).getId();
    }
}
