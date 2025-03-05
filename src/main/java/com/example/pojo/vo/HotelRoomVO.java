package com.example.pojo.vo;

import com.example.pojo.entity.HotelRoom;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿房间VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelRoomVO extends HotelRoom {
    /**
     * 民宿名称
     */
    private String hotelName;
}
