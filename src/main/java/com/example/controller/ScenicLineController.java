package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicLineQueryDto;
import com.example.pojo.entity.ScenicLine;
import com.example.pojo.vo.ScenicLineVO;
import com.example.service.ScenicLineService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点路线控制器
 */
@RestController
@RequestMapping("/scenicLine")
public class ScenicLineController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicLineService scenicLineService;

    /**
     * 景点路线新增
     *
     * @param scenicLine 景点路线实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody ScenicLine scenicLine) {
        return scenicLineService.save(scenicLine);
    }

    /**
     * 景点路线修改
     *
     * @param scenicLine 景点路线实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody ScenicLine scenicLine) {
        return scenicLineService.update(scenicLine);
    }

    /**
     * 景点路线批量删除
     *
     * @param ids 景点路线ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return scenicLineService.batchDelete(ids);
    }

    /**
     * 查询景点路线
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicLineVO>> query(@RequestBody ScenicLineQueryDto dto) {
        return scenicLineService.query(dto);
    }

}
