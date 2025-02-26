package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点分类查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicCategoryQueryDto extends QueryDto {
    private String name;
}
