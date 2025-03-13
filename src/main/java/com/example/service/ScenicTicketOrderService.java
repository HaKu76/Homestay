package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketOrderQueryDto;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.ScenicTicketOrderVO;

import java.util.List;

public interface ScenicTicketOrderService {
    Result<Void> save(ScenicTicketOrder scenicTicketOrder);

    Result<Void> update(ScenicTicketOrder scenicTicketOrder);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<ScenicTicketOrderVO>> query(ScenicTicketOrderQueryDto dto);

    Result<List<ScenicTicketOrderVO>> queryScenicTicketOrder(ScenicTicketOrderQueryDto dto);
}
