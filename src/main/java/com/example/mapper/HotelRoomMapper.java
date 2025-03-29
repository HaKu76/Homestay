package com.example.mapper;


import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.entity.HotelRoom;
import com.example.pojo.vo.HotelRoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 民宿持久化接口
 */
@Mapper
public interface HotelRoomMapper {
    void save(HotelRoom hotelRoom);

    void update(HotelRoom hotelRoom);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<HotelRoomVO> queryVendorRoom(HotelRoomQueryDto dto);

    Integer queryVendorRoomCount(HotelRoomQueryDto dto);

    List<HotelRoomVO> query(HotelRoomQueryDto dto);

    // 分页页数
    Integer queryCount(HotelRoomQueryDto dto);

    List<HotelRoomVO> queryByHotelIds(@Param(value = "ids") List<Integer> ids);
}
