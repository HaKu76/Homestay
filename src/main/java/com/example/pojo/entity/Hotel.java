package com.example.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 民宿实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 民宿名称
     */
    private String name;

    /**
     * 民宿封面
     */
    private String cover;

    /**
     * 民宿地址
     */
    private String address;

    /**
     * 民宿联系电话
     */
    private String concatPhone;

    /**
     * 供应商ID
     */
    private Integer vendorId;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
