package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点攻略查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicStrategyQueryDto extends QueryDto {
    /**
     * 用户ID
     */
    private String userID;
    /**
     * 景点ID
     */
    private Integer scenicId;
    /**
     * 攻略标题
     */
    private String title;
    /**
     * 是否已审核
     */
    private Boolean isAudit;
}
