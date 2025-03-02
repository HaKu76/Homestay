package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点评分查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicRatingQueryDto extends QueryDto {
    /**
     * 景点ID
     */
    private Integer scenicId;
    /**
     * 用户ID
     */
    private Integer userId;
}
