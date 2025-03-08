package com.example.mapper;

import com.example.pojo.dto.query.extend.HotelOrderInfoQueryDto;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.HotelOrderInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 民宿订单持久化接口
 */
@Mapper
public interface HotelOrderInfoMapper {
    void save(HotelOrderInfo hotelOrderInfo);

    void update(HotelOrderInfo hotelOrderInfo);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<HotelOrderInfoVO> query(HotelOrderInfoQueryDto dto);

    // 分页页数
    Integer queryCount(HotelOrderInfoQueryDto dto);
}
