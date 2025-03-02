package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点路线查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicLineQueryDto extends QueryDto {
    /**
     * 景点路线名称
     */
    private String name;
    /**
     * 景点ID
     */
    private Integer scenicId;
}
