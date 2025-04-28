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
 * 评论管理接口
 * 包含评论的增删改查等操作
 */
@RestController
@RequestMapping("/evaluations")
public class EvaluationsController {

    @Resource
    private EvaluationsService evaluationsService;

    /**
     * 创建新评论
     * - 需要登录权限
     * - 接收JSON格式评论数据
     */
    @Protector
    @PostMapping("/insert")
    public Result<Object> insert(@RequestBody Evaluations evaluations) {
        return evaluationsService.insert(evaluations);
    }

    /**
     * 修改评论内容
     * - 需要登录权限
     * - 根据评论ID更新数据
     */
    @Protector
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Evaluations evaluations) {
        return evaluationsService.update(evaluations);
    }

    /**
     * 获取指定内容的评论列表
     * - 需要登录权限
     *
     * @param contentId   内容ID（如文章ID）
     * @param contentType 内容类型（如article）
     */
    @Protector
    @GetMapping("/list/{contentId}/{contentType}")
    public Result<Object> list(@PathVariable Integer contentId,
                               @PathVariable String contentType) {
        return evaluationsService.list(contentId, contentType);
    }

    /**
     * 分页搜索评论
     * - 自动处理分页参数
     *
     * @param evaluationsQueryDto 包含搜索条件和分页参数
     */
    @Pager
    @PostMapping("/query")
    public Result<Object> query(@RequestBody EvaluationsQueryDto evaluationsQueryDto) {
        return evaluationsService.query(evaluationsQueryDto);
    }

    /**
     * 批量删除评论
     *
     * @param ids 要删除的评论ID集合
     */
    @PostMapping("/batchDelete")
    public Result<Object> batchDelete(@RequestBody List<Integer> ids) {
        return evaluationsService.batchDelete(ids);
    }

    /**
     * 删除单个评论
     * - 需要登录权限
     *
     * @param id 评论ID
     */
    @Protector
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        return evaluationsService.delete(id);
    }
}


