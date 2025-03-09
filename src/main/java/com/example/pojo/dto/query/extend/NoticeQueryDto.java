package com.example.pojo.dto.query.extend;

import com.example.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeQueryDto extends QueryDto {
    /**
     * 公告标题
     */
    private String title;
}
