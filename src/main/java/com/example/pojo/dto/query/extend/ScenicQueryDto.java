package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicQueryDto extends QueryDto {
    /**
     * 景点名称
     */
    private String name;
    /**
     * 供应商ID
     */
    private Integer vendorId;
    /**
     * 景点分类ID
     */
    private Integer categoryId;
    /**
     * 景点状态
     */
    private Boolean status;
    /**
     * 收藏者用户ID列表
     */
    private String saveIds;
}
