package com.example.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vendor {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 办公地址
     */
    private String workAddress;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 是否已经审核
     */
    private Boolean isAudit;

    /**
     * 供应商状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}