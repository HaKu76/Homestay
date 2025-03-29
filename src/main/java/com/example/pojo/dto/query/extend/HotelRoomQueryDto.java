package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 民宿房间查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelRoomQueryDto extends QueryDto {
    /**
     * 景点房间名称
     */
    private String name;
    /**
     * 酒店ID
     */
    private Integer hotelId;
    /**
     * 酒店ID集合
     */
    private List<Integer> hotelIds;

}
