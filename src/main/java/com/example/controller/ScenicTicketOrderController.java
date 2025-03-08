package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketOrderQueryDto;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.ScenicTicketOrderVO;
import com.example.service.ScenicTicketOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点门票订单控制器
 */
@RestController
@RequestMapping("/scenicTicketOrder")
public class ScenicTicketOrderController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicTicketOrderService scenicTicketOrderService;

    /**
     * 景点门票订单新增
     *
     * @param scenicTicketOrder 景点门票订单实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody ScenicTicketOrder scenicTicketOrder) {
        return scenicTicketOrderService.save(scenicTicketOrder);
    }

    /**
     * 景点门票订单修改
     *
     * @param scenicTicketOrder 景点门票订单实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody ScenicTicketOrder scenicTicketOrder) {
        return scenicTicketOrderService.update(scenicTicketOrder);
    }

    /**
     * 景点门票订单批量删除
     *
     * @param ids 景点门票订单ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return scenicTicketOrderService.batchDelete(ids);
    }

    /**
     * 查询景点门票订单
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicTicketOrderVO>> query(@RequestBody ScenicTicketOrderQueryDto dto) {
        return scenicTicketOrderService.query(dto);
    }

}
