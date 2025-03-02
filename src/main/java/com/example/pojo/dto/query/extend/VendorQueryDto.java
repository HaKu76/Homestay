package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VendorQueryDto extends QueryDto {
    /**
     * 供应商名称
     */
    private String name;

    /**
     * 是否已经审核
     */
    private Boolean isAudit;
    /**
     * 供应商状态
     */
    private Boolean status;
}
