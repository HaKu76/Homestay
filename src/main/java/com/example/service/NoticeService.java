package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.NoticeQueryDto;
import com.example.pojo.entity.Notice;

import java.util.List;

public interface NoticeService {
    Result<Void> save(Notice notice);

    Result<Void> update(Notice notice);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<Notice>> query(NoticeQueryDto dto);
}
