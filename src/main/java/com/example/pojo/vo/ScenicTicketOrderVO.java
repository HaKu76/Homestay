package com.example.pojo.vo;

import com.example.pojo.entity.ScenicTicketOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点订单信息VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicTicketOrderVO extends ScenicTicketOrder {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 景点ID
     */
    private String scenicId;
    /**
     * 景点名称
     */
    private String scenicName;
    /**
     * 门票介绍
     */
    private String detail;
    /**
     * 数量
     */
    private Integer number;
    /**
     * 单价
     */
    private Double price;
    /**
     * 折扣
     */
    private Double discount;
}
