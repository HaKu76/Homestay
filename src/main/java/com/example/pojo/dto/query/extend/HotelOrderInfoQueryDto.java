package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿订单查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelOrderInfoQueryDto extends QueryDto {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 房间ID
     */
    private Integer roomId;
    /**
     * 支付状态
     */
    private Boolean payStatus;
}
