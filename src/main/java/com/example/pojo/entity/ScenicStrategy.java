package com.example.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 景点评分实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScenicStrategy {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 景点ID
     */
    private Integer scenicId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 景点攻略封面图
     */
    private String cover;

    /**
     * 景点标题
     */
    private String title;

    /**
     * 景点攻略内容
     */
    private String content;

    /**
     * 是否已经审核
     */
    private Boolean isAudit;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}