package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.service.ScenicService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点控制器
 */
@RestController
@RequestMapping("/scenic")
public class ScenicController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicService scenicService;

    /**
     * 景点新增
     *
     * @param scenic 景点实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody Scenic scenic) {
        return scenicService.save(scenic);
    }

    /**
     * 景点修改
     *
     * @param scenic 景点实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Scenic scenic) {
        return scenicService.update(scenic);
    }

    /**
     * 景点批量删除
     *
     * @param ids 景点ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return scenicService.batchDelete(ids);
    }

    /**
     * 查询景点
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/queryVendorScenic")
    @ResponseBody
    public Result<List<ScenicVO>> queryVendorScenic(@RequestBody ScenicQueryDto dto) {
        return scenicService.queryVendorScenic(dto);
    }

    /**
     * 查询景点
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicVO>> query(@RequestBody ScenicQueryDto dto) {
        return scenicService.query(dto);
    }
}
