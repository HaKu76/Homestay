package com.example.pojo.vo;

import com.example.pojo.entity.HotelOrderInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿订单信息VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelOrderInfoVO extends HotelOrderInfo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 房间号
     */
    private String roomName;
    /**
     * 民宿名
     */
    private String hotelName;
}
