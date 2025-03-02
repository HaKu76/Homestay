package com.example.service.impl;


import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Vendor;
import com.example.pojo.vo.VendorVO;
import com.example.service.VendorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商的业务逻辑实现类
 */
@Service
public class VendorServiceImpl implements VendorService {

    @Resource
    private VendorMapper vendorMapper;

    /**
     * 供应商新增
     *
     * @param vendor 供应商实体
     * @return
     */
    @Override
    public Result<Void> save(Vendor vendor) {
        //设置创建时间
        vendor.setCreateTime(LocalDateTime.now());
        //设置供应商初始状态为已审核
        vendor.setStatus(true);
        vendorMapper.save(vendor);
        return ApiResult.success();
    }

    /**
     * 供应商修改
     *
     * @param vendor 供应商实体
     * @return
     */
    @Override
    public Result<Void> update(Vendor vendor) {
        vendorMapper.update(vendor);
        return ApiResult.success();
    }

    /**
     * 供应商删除
     *
     * @param ids 供应商ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        vendorMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 供应商查询
     *
     * @param dto 查询实体
     * @return Result<List < Vendor>>
     */
    @Override
    public Result<List<VendorVO>> query(VendorQueryDto dto) {
        Integer totalCount = vendorMapper.queryCount(dto);
        List<VendorVO> result = vendorMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
