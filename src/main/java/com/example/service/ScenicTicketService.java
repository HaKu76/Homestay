package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketQueryDto;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.vo.ScenicTicketVO;

import java.util.List;

public interface ScenicTicketService {
    Result<Void> save(ScenicTicket scenicTicket);

    Result<Void> update(ScenicTicket scenicTicket);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<ScenicTicketVO>> query(ScenicTicketQueryDto dto);
}
