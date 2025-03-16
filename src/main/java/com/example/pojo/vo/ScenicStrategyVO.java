package com.example.pojo.vo;

import com.example.pojo.entity.ScenicStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点攻略VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicStrategyVO extends ScenicStrategy {
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 景点名称
     */
    private String scenicName;
}
