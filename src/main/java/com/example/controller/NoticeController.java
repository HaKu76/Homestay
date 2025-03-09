package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.NoticeQueryDto;
import com.example.pojo.entity.Notice;
import com.example.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private NoticeService noticeService;

    /**
     * 公告新增
     *
     * @param notice 公告实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody Notice notice) {
        return noticeService.save(notice);
    }

    /**
     * 公告修改
     *
     * @param notice 公告实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Notice notice) {
        return noticeService.update(notice);
    }

    /**
     * 公告批量删除
     *
     * @param ids 公告ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return noticeService.batchDelete(ids);
    }

    /**
     * 查询公告
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<Notice>> query(@RequestBody NoticeQueryDto dto) {
        return noticeService.query(dto);
    }

}
