package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Vendor;
import com.example.pojo.vo.VendorVO;

import java.util.List;

public interface VendorService {
    Result<Void> save(Vendor vendor);

    Result<Void> update(Vendor vendor);

    Result<Void> batchDelete(List<Integer> ids);

    Result<List<VendorVO>> query(VendorQueryDto dto);
}
