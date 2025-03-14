package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.ScenicTicketMapper;
import com.example.mapper.ScenicTicketOrderMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.dto.query.extend.*;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.*;
import com.example.service.ScenicTicketOrderService;
import com.example.utils.DateUtil;
import com.example.utils.MoneyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点门票订单的业务逻辑实现类
 */
@Service
public class ScenicTicketOrderServiceImpl implements ScenicTicketOrderService {

    @Resource
    private ScenicTicketOrderMapper scenicTicketOrderMapper;
    @Resource
    private VendorMapper vendorMapper;
    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private ScenicTicketMapper scenicTicketMapper;

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

    /**
     * 查询供应商管理的全部景点门票订单
     *
     * @return Result<Void>
     */
    @Override
    public Result<List<ScenicTicketOrderVO>> queryScenicTicketOrder(ScenicTicketOrderQueryDto dto) {
        // 取出供应商信息
        Integer vendorId = getVendorId();
        // 查供应商管理的全部的景点信息
        ScenicQueryDto queryDto = new ScenicQueryDto();
        queryDto.setVendorId(vendorId);
        List<ScenicVO> scenicVOS = scenicMapper.query(queryDto);
        // 取出里面的全部景点ID
        List<Integer> scenicIds = scenicVOS.stream()
                .map(ScenicVO::getId)
                .collect(Collectors.toList());
        // 用户ID --- 供应商ID --- 景点ID --- 门票ID --- 订单信息
        List<ScenicTicketVO> scenicTicketVOS
                = scenicTicketMapper.queryByScenicIds(scenicIds);
        // 获取门票的Id列表
        List<Integer> scenicTicketIds = scenicTicketVOS.stream()
                .map(ScenicTicketVO::getId)
                .collect(Collectors.toList());
        ScenicTicketQueryParamDto orderQueryParam = new ScenicTicketQueryParamDto(scenicTicketIds, dto);
        List<ScenicTicketOrderVO> scenicTicketOrderVOS =
                scenicTicketOrderMapper.queryByScenicIds(orderQueryParam);
        Integer totalCount =
                scenicTicketOrderMapper.queryCountByScenicIds(orderQueryParam);
        return ApiResult.success(scenicTicketOrderVOS, totalCount);
    }

    /**
     * 统计近期成交的金额
     *
     * @param day 往前面查的天数
     * @return Result<List < ChartVO>>
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        // 获取时间范围
        QueryDto queryDto = DateUtil.startAndEndTime(day);
        ScenicTicketOrderQueryDto dto = new ScenicTicketOrderQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());
        Result<List<ScenicTicketOrderVO>> scenicTicketOrder
                = queryScenicTicketOrder(dto);
        ApiResult<List<ScenicTicketOrderVO>> response = (ApiResult) scenicTicketOrder;
        List<ScenicTicketOrderVO> data = response.getData();
        List<MoneyDto> moneyDtoList = data.stream().map(scenicTicketOrderVO -> new MoneyDto(
                scenicTicketOrderVO.getAmount(),
                scenicTicketOrderVO.getPayTime()
        )).collect(Collectors.toList());
        List<ChartVO> chartVOS = MoneyUtils.countMoney(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 取得供应商ID
     *
     * @return Integer
     */
    private Integer getVendorId() {
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        // 当前操作者的用户ID
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 1. 要么真的没有 2. 有的话，只有一项
        if (CollectionUtils.isEmpty(vendorVOS)) {
            return null;
        }
        return vendorVOS.get(0).getId();
    }

}
