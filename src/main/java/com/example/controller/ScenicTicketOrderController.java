package com.example.controller;

import com.example.aop.Pager;
import com.example.context.LocalThreadHolder;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketOrderQueryDto;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.ChartVO;
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
     * 查询供应商管理的全部景点门票订单
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/queryScenicTicketOrder")
    @ResponseBody
    public Result<List<ScenicTicketOrderVO>> queryScenicTicketOrder(@RequestBody ScenicTicketOrderQueryDto dto) {
        return scenicTicketOrderService.queryScenicTicketOrder(dto);
    }

    /**
     * 门票订单支付
     *
     * @return Result<List < ScenicTicketOrderVO>>
     */
    @PostMapping(value = "/pay")
    @ResponseBody
    public Result<Void> pay(@RequestBody ScenicTicketOrder scenicTicketOrder) {
        return scenicTicketOrderService.pay(scenicTicketOrder);
    }

    /**
     * 查询用户的景点门票数据
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/queryUser")
    @ResponseBody
    public Result<List<ScenicTicketOrderVO>> queryUser(@RequestBody ScenicTicketOrderQueryDto dto) {
        // 设置上用户ID
        dto.setUserId(LocalThreadHolder.getUserId());
        return scenicTicketOrderService.query(dto);
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

    /**
     * 统计成交金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQueryUser/{day}")
    @ResponseBody
    public Result<List<ChartVO>> daysQueryUser(@PathVariable Integer day) {
        return scenicTicketOrderService.daysQueryUser(day);
    }

    /**
     * 统计全站指定日期里面的成交门票金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQueryMoney/{day}")
    @ResponseBody
    public Result<List<ChartVO>> daysQueryMoney(@PathVariable Integer day) {
        return scenicTicketOrderService.daysQueryMoney(day);
    }

    /**
     * 统计成交金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQuery/{day}")
    @ResponseBody
    public Result<List<ChartVO>> query(@PathVariable Integer day) {
        return scenicTicketOrderService.daysQuery(day);
    }

}
