package com.example.pojo.dto.query.extend;

import lombok.Data;

import java.util.List;

/**
 * 供应商查询门票订单参数入参
 */
@Data
public class ScenicTicketQueryParamDto {

    /**
     * 门票的ID列表
     */
    private List<Integer> ticketIds;
    /**
     * 查询门票订单的参数实体
     */
    private ScenicTicketOrderQueryDto scenicTicketOrderQueryDto;


    public ScenicTicketQueryParamDto(List<Integer> ticketIds, ScenicTicketOrderQueryDto scenicTicketOrderQueryDto) {
        this.ticketIds = ticketIds;
        this.scenicTicketOrderQueryDto = scenicTicketOrderQueryDto;
    }
}
