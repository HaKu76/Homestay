package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.vo.HotelVO;

import java.util.List;

public interface HotelService {
    Result<Void> save(Hotel hotel);

    Result<Void> update(Hotel hotel);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<HotelVO>> query(HotelQueryDto dto);
}
