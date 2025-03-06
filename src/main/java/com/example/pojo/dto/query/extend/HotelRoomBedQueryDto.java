package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿房间床位查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelRoomBedQueryDto extends QueryDto {
    /**
     * 民宿房间ID
     */
    private String roomId;
    /**
     * 状态
     */
    private Boolean status;
}
