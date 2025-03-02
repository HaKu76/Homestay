package com.example.pojo.vo;

import com.example.pojo.entity.ScenicLine;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点路线VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicLineVO extends ScenicLine {
    /**
     * 景点名称
     */
    private String scenicName;
    /**
     * 景点封面
     */
    private String scenicCover;
    /**
     * 景点所在地址
     */
    private String scenicAddress;
}
