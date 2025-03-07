package com.example.pojo.vo;

import com.example.pojo.entity.ScenicTicket;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点门票VO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScenicTicketVO extends ScenicTicket {
    /**
     * 景点名称
     */
    private String scenicName;
}
