package com.example.controller;

import com.example.aop.Pager;
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
     * 供应商新增
     *
     * @param vendor 供应商实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody Vendor vendor) {
        return vendorService.save(vendor);
    }

    /**
     * 供应商修改
     *
     * @param vendor 供应商实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Vendor vendor) {
        return vendorService.update(vendor);
    }

    /**
     * 供应商批量删除
     *
     * @param ids 供应商ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return vendorService.batchDelete(ids);
    }

    /**
     * 查询供应商
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<VendorVO>> query(@RequestBody VendorQueryDto dto) {
        return vendorService.query(dto);
    }

}
