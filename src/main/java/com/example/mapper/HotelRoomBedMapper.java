package com.example.mapper;


import com.example.pojo.dto.query.extend.HotelRoomBedQueryDto;
import com.example.pojo.entity.HotelRoomBed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 民宿房间床位持久化接口
 */
@Mapper
public interface HotelRoomBedMapper {
    void save(HotelRoomBed hotelRoomBed);

    void update(HotelRoomBed hotelRoomBed);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<HotelRoomBed> query(HotelRoomBedQueryDto dto);

    // 分页页数
    Integer queryCount(HotelRoomBedQueryDto dto);
}
