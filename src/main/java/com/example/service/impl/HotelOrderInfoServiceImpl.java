package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.HotelMapper;
import com.example.mapper.HotelOrderInfoMapper;
import com.example.mapper.HotelRoomMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.dto.query.extend.*;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.*;
import com.example.service.HotelOrderInfoService;
import com.example.utils.DateUtil;
import com.example.utils.DecimalUtils;
import com.example.utils.MoneyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 民宿订单的业务逻辑实现类
 */
@Service
public class HotelOrderInfoServiceImpl implements HotelOrderInfoService {

    @Resource
    private HotelOrderInfoMapper hotelOrderInfoMapper;
    @Resource
    private VendorMapper vendorMapper;
    @Resource
    private HotelMapper hotelMapper;
    @Resource
    private HotelRoomMapper hotelRoomMapper;

    /**
     * 民宿订单新增
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(HotelOrderInfo hotelOrderInfo) {
        HotelRoomQueryDto hotelRoomQueryDto = new HotelRoomQueryDto();
        hotelRoomQueryDto.setId(hotelOrderInfo.getRoomId());
        List<HotelRoomVO> hotelRoomVOS = hotelRoomMapper.query(hotelRoomQueryDto);
        if (hotelRoomVOS.isEmpty()) {
            return ApiResult.error("房间信息未找到");
        }
        HotelRoomVO hotelRoomVO = hotelRoomVOS.get(0);
        Double discount = hotelRoomVO.getDiscount();
        // 没有设计买多少晚，这里使用1就行
        BigDecimal amount = DecimalUtils.calculateTotal(
                1,
                hotelRoomVO.getPrice(),
                discount == null ? 1 : (discount / 10)
        );
        hotelOrderInfo.setAmount(amount);
        // 设置用户ID
        hotelOrderInfo.setUserId(LocalThreadHolder.getUserId());
        //设置创建时间
        hotelOrderInfo.setCreateTime(LocalDateTime.now());
        // 设置默认支付状态为未支付
        hotelOrderInfo.setPayStatus(false);
        hotelOrderInfoMapper.save(hotelOrderInfo);
        return ApiResult.success();
    }

    /**
     * 民宿订单修改
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return
     */
    @Override
    public Result<Void> update(HotelOrderInfo hotelOrderInfo) {
        hotelOrderInfoMapper.update(hotelOrderInfo);
        return ApiResult.success();
    }

    /**
     * 民宿订单删除
     *
     * @param ids 民宿订单ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        hotelOrderInfoMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 民宿订单查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<HotelOrderInfoVO>> query(HotelOrderInfoQueryDto dto) {
        Integer totalCount = hotelOrderInfoMapper.queryCount(dto);
        List<HotelOrderInfoVO> result = hotelOrderInfoMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }

    /**
     * 查询供应商名下的民宿订单
     *
     * @return Result<HotelOrderInfoVO>
     */
    @Override
    public Result<List<HotelOrderInfoVO>> queryVendorHotelOrder(HotelOrderInfoQueryDto dto) {
        // 链路： 用户ID  --- 供应商  --- 名下管理的民宿 --- 房间 --- 订单（用户下单）
        // 当前操作者的用户ID
        Integer userId = LocalThreadHolder.getUserId();
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(userId);
        // 名下管理的供应商信息
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 这是没有的判断，反之，过了if，一定存在并且只有一项
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商信息异常");
        }
        VendorVO vendorVO = vendorVOS.get(0);
        // 只有当供应商已经审核并且状态是正常的，才有资格查
        if (!vendorVO.getStatus() || !vendorVO.getIsAudit()) {
            return ApiResult.error("供应商状态异常或无查看权限");
        }
        // 查询名下的民宿信息
        HotelQueryDto hotelQueryDto = new HotelQueryDto();
        hotelQueryDto.setVendorId(vendorVO.getId());
        List<HotelVO> hotelVOS = hotelMapper.query(hotelQueryDto);
        if (hotelVOS.isEmpty()) {
            return ApiResult.error("名下无管理民宿");
        }
        // 过滤取得民宿的Id集合列表
        List<Integer> hotelIds = hotelVOS.stream()
                .map(HotelVO::getId)
                .collect(Collectors.toList());
        // 通过民宿的的这一批ID，去获取他们名下的房间信息
        List<HotelRoomVO> hotelRoomVOS = hotelRoomMapper.queryByHotelIds(hotelIds);
        // 过滤获取房间的ID集合列表
        List<Integer> hotelRoomIds = hotelRoomVOS.stream()
                .map(HotelRoomVO::getId)
                .collect(Collectors.toList());
        // 构造查询条件
        HotelOrderInfoQueryParamDto paramDto = new HotelOrderInfoQueryParamDto(
                hotelRoomIds,
                dto
        );
        // 查询指定房间下的所有符合条件订单
        List<HotelOrderInfoVO> orderInfoVOList
                = hotelOrderInfoMapper.queryByHotelRoomIds(paramDto);
        Integer totalCount
                = hotelOrderInfoMapper.queryCountByHotelRoomIds(paramDto);
        return ApiResult.success(orderInfoVOList, totalCount);
    }

    /**
     * 统计成交金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        // 获取时间范围
        QueryDto queryDto = DateUtil.startAndEndTime(day);
        HotelOrderInfoQueryDto dto = new HotelOrderInfoQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());
        Result<List<HotelOrderInfoVO>> hotelOrderVendor
                = queryVendorHotelOrder(dto);
        ApiResult<List<HotelOrderInfoVO>> response = (ApiResult) hotelOrderVendor;
        List<HotelOrderInfoVO> data = response.getData();
        List<MoneyDto> moneyDtoList = data.stream().map(hotelOrderInfoVO -> new MoneyDto(
                hotelOrderInfoVO.getAmount(),
                hotelOrderInfoVO.getPayTime()
        )).collect(Collectors.toList());
        List<ChartVO> chartVOS = MoneyUtils.countMoney(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 统计全站指定日期里面的成交门票金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @Override
    public Result<List<ChartVO>> daysQueryMoney(Integer day) {
        // 获取时间范围
        QueryDto queryDto = DateUtil.startAndEndTime(day);
        HotelOrderInfoQueryDto dto = new HotelOrderInfoQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());
        List<HotelOrderInfoVO> orderInfoVOS = hotelOrderInfoMapper.query(dto);
        List<MoneyDto> moneyDtoList = orderInfoVOS.stream().map(hotelOrderInfoVO -> new MoneyDto(
                hotelOrderInfoVO.getAmount(),
                hotelOrderInfoVO.getPayTime()
        )).collect(Collectors.toList());
        List<ChartVO> chartVOS = MoneyUtils.countMoney(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 民宿订单支付
     *
     * @param hotelOrderInfo 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> pay(HotelOrderInfo hotelOrderInfo) {
        hotelOrderInfo.setPayStatus(true);
        hotelOrderInfo.setPayTime(LocalDateTime.now());
        hotelOrderInfoMapper.update(hotelOrderInfo);
        return ApiResult.success();
    }
}
