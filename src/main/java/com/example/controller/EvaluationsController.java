package com.example.controller;

import com.example.aop.Pager;
import com.example.aop.Protector;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.EvaluationsQueryDto;
import com.example.pojo.entity.Evaluations;
import com.example.service.EvaluationsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论 Controller
 */
@RestController
@RequestMapping("/evaluations")
public class EvaluationsController {

    @Resource
    private EvaluationsService evaluationsService;

    /**
     * 评论
     *
     * @return Result<String>
     */
    @Protector
    @PostMapping(value = "/insert")
    @ResponseBody
    public Result<Object> insert(@RequestBody Evaluations evaluations) {
        return evaluationsService.insert(evaluations);
    }

    /**
     * 评论修改
     *
     * @return Result<String>
     */
    @Protector
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Evaluations evaluations) {
        return evaluationsService.update(evaluations);
    }

    /**
     * 查询内容下的全部评论
     *
     * @return Result<String>
     */
    @Protector
    @GetMapping(value = "/list/{contentId}/{contentType}")
    @ResponseBody
    public Result<Object> list(@PathVariable Integer contentId,
                               @PathVariable String contentType) {
        return evaluationsService.list(contentId, contentType);
    }

    /**
     * 分页查询评论
     *
     * @return Result<String>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<Object> query(@RequestBody EvaluationsQueryDto evaluationsQueryDto) {
        return evaluationsService.query(evaluationsQueryDto);
    }

    /**
     * 批量删除评论数据
     *
     * @return Result<String>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Object> batchDelete(@RequestBody List<Integer> ids) {
        return evaluationsService.batchDelete(ids);
    }

    /**
     * 通过ID删除评论信息
     *
     * @return Result<String>
     */
    @Protector
    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public Result<String> delete(@PathVariable Integer id) {
        return evaluationsService.delete(id);
    }

}

