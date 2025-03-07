package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点门票查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicTicketQueryDto extends QueryDto {
    /**
     * 景点ID
     */
    private String scenicId;
    /**
     * 门票介绍
     */
    private String detail;
    /**
     * 门票可用状态
     */
    private Boolean useStatus;
}
