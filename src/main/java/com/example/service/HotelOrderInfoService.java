package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelOrderInfoQueryDto;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.HotelOrderInfoVO;

import java.util.List;

public interface HotelOrderInfoService {
    Result<Void> save(HotelOrderInfo hotelOrderInfo);

    Result<Void> update(HotelOrderInfo hotelOrderInfo);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<HotelOrderInfoVO>> query(HotelOrderInfoQueryDto dto);
}
