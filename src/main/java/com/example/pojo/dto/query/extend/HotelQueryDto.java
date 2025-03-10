package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelQueryDto extends QueryDto {
    /**
     * 民宿名称
     */
    private String name;
    /**
     * 供应商ID
     */
    private Integer vendorId;
}
