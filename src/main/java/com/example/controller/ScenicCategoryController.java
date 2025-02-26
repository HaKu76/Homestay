package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicCategoryQueryDto;
import com.example.pojo.entity.ScenicCategory;
import com.example.service.ScenicCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点分类控制器
 */
@RestController
@RequestMapping("/scenicCategory")
public class ScenicCategoryController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicCategoryService scenicCategoryService;

    /**
     * 景点分类新增
     * @param scenicCategory 景点分类实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody ScenicCategory scenicCategory){
        return scenicCategoryService.save(scenicCategory);
    }

    /**
     * 景点分类修改
     * @param scenicCategory 景点分类实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody ScenicCategory scenicCategory){
        return scenicCategoryService.update(scenicCategory);
    }

    /**
     * 景点分类批量删除
     * @param ids 景点分类ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids){
        return scenicCategoryService.batchDelete(ids);
    }

    /**
     * 查询景点分类
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicCategory>> query(@RequestBody ScenicCategoryQueryDto dto){
        return scenicCategoryService.query(dto);
    }

}
