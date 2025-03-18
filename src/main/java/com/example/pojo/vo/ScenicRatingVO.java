package com.example.pojo.vo;

import com.example.pojo.entity.ScenicRating;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点评分的出参类VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScenicRatingVO extends ScenicRating {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
}
