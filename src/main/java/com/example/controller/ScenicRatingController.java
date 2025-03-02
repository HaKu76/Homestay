package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;
import com.example.service.ScenicRatingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点评分控制器
 */
@RestController
@RequestMapping("/scenicRating")
public class ScenicRatingController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicRatingService scenicRatingService;

    /**
     * 景点评分新增
     *
     * @param scenicRating 景点评分实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody ScenicRating scenicRating) {
        return scenicRatingService.save(scenicRating);
    }

    /**
     * 查询景点评分
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicRating>> query(@RequestBody ScenicRatingQueryDto dto) {
        return scenicRatingService.query(dto);
    }
}
