package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomBedQueryDto;
import com.example.pojo.entity.HotelRoomBed;

import java.util.List;

public interface HotelRoomBedService {
    Result<Void> save(HotelRoomBed hotelRoomBed);

    Result<Void> update(HotelRoomBed hotelRoomBed);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<HotelRoomBed>> query(HotelRoomBedQueryDto dto);
}
