package com.example.pojo.vo;

import com.example.pojo.entity.Hotel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民宿VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotelVO extends Hotel {
    /**
     * 供应商名称
     */
    private String vendorName;
}
