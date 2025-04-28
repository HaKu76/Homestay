package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.SelectedVO;
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
     * 新增景点
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
     * 修改景点
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
     * 批量删除景点
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
     * 查询供应商景点
     *
     * @param dto 查询条件
     * @return Result<List < ScenicVO>>
     */
    @Pager
    @PostMapping(value = "/queryVendorScenic")
    @ResponseBody
    public Result<List<ScenicVO>> queryVendorScenic(@RequestBody ScenicQueryDto dto) {
        return scenicService.queryVendorScenic(dto);
    }

    /**
     * 查询景点下拉选择器
     *
     * @return Result<List < SelectedVO>>
     */
    @GetMapping(value = "/querySelectedScenic")
    @ResponseBody
    public Result<List<SelectedVO>> querySelectedScenic() {
        return scenicService.querySelectedScenic();
    }

    /**
     * 浏览操作
     *
     * @param scenicId 景点ID
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/viewOperation/{scenicId}")
    @ResponseBody
    public Result<Void> viewOperation(@PathVariable Integer scenicId) {
        return scenicService.viewOperation(scenicId);
    }

    /**
     * 收藏操作
     *
     * @param scenicId 景点ID
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/saveOperation/{scenicId}")
    @ResponseBody
    public Result<Void> saveOperation(@PathVariable Integer scenicId) {
        return scenicService.saveOperation(scenicId);
    }

    /**
     * 查询用户收藏的景点状况
     *
     * @param scenicId 景点ID
     * @return Result<Boolean>
     */
    @Pager
    @PostMapping(value = "/saveStatus/{scenicId}")
    @ResponseBody
    public Result<Boolean> saveStatus(@PathVariable Integer scenicId) {
        return scenicService.saveStatus(scenicId);
    }

    /**
     * 查询用户收藏的景点信息
     *
     * @return Result<List < ScenicVO>>
     */
    @Pager
    @GetMapping(value = "/querySave")
    @ResponseBody
    public Result<List<ScenicVO>> querySave() {
        return scenicService.querySave();
    }

    /**
     * 查询景点
     *
     * @param dto 查询条件
     * @return Result<List < ScenicVO>>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicVO>> query(@RequestBody ScenicQueryDto dto) {
        return scenicService.query(dto);
    }
}
