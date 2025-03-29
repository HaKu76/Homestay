package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.entity.HotelRoom;
import com.example.pojo.vo.HotelRoomVO;

import java.util.List;

public interface HotelRoomService {
    Result<Void> save(HotelRoom hotelRoom);

    Result<Void> update(HotelRoom hotelRoom);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<HotelRoomVO>> query(HotelRoomQueryDto dto);

    Result<List<HotelRoomVO>> queryVendorRoom(HotelRoomQueryDto dto);
}
