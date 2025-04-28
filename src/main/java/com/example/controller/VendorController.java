package com.example.controller;

import com.example.aop.Pager;
import com.example.context.LocalThreadHolder;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Vendor;
import com.example.pojo.vo.VendorVO;
import com.example.service.VendorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 供应商控制器
 */
@RestController
@RequestMapping("/vendor")
public class VendorController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private VendorService vendorService;

    /**
     * 新增供应商
     *
     * @param vendor 供应商实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Vendor vendor) {
        return vendorService.save(vendor);
    }

    /**
     * 修改供应商
     *
     * @param vendor 供应商实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Vendor vendor) {
        return vendorService.update(vendor);
    }

    /**
     * 批量删除供应商
     *
     * @param ids 供应商ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return vendorService.batchDelete(ids);
    }

    /**
     * 查询用户关联的供应商信息
     *
     * @return Result<List < VendorVO>>
     */
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<VendorVO>> query() {
        VendorQueryDto queryDto = new VendorQueryDto();
        // 设置用户ID
        queryDto.setUserId(LocalThreadHolder.getUserId());
        return vendorService.query(queryDto);
    }

    /**
     * 查询供应商
     *
     * @return Result<List < VendorVO>>
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<VendorVO>> query(@RequestBody VendorQueryDto dto) {
        return vendorService.query(dto);
    }

}
