package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.HotelOrderInfoMapper;
import com.example.mapper.HotelRoomMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelOrderInfoQueryDto;
import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.HotelOrderInfoVO;
import com.example.pojo.vo.HotelRoomVO;
import com.example.service.HotelOrderInfoService;
import com.example.utils.DecimalUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 民宿订单的业务逻辑实现类
 */
@Service
public class HotelOrderInfoServiceImpl implements HotelOrderInfoService {

    @Resource
    private HotelOrderInfoMapper hotelOrderInfoMapper;
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
}
