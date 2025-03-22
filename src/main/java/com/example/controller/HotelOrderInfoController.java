package com.example.controller;

import com.example.aop.Pager;
import com.example.context.LocalThreadHolder;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelOrderInfoQueryDto;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.ChartVO;
import com.example.pojo.vo.HotelOrderInfoVO;
import com.example.service.HotelOrderInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 民宿订单控制器
 */
@RestController
@RequestMapping("/hotelOrderInfo")
public class HotelOrderInfoController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private HotelOrderInfoService hotelOrderInfoService;

    /**
     * 民宿订单新增
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody HotelOrderInfo hotelOrderInfo) {
        return hotelOrderInfoService.save(hotelOrderInfo);
    }

    /**
     * 民宿订单修改
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody HotelOrderInfo hotelOrderInfo) {
        return hotelOrderInfoService.update(hotelOrderInfo);
    }

    /**
     * 民宿订单批量删除
     *
     * @param ids 民宿订单ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return hotelOrderInfoService.batchDelete(ids);
    }

    /**
     * 查询民宿订单
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<HotelOrderInfoVO>> query(@RequestBody HotelOrderInfoQueryDto dto) {
        return hotelOrderInfoService.query(dto);
    }

    /**
     * 查询供应商名下的民宿订单
     *
     * @return Result<HotelOrderInfoVO>
     */
    @Pager
    @PostMapping(value = "/queryVendorHotelOrder")
    @ResponseBody
    public Result<List<HotelOrderInfoVO>> queryVendorHotelOrder(@RequestBody HotelOrderInfoQueryDto dto) {
        return hotelOrderInfoService.queryVendorHotelOrder(dto);
    }

    /**
     * 民宿订单支付
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/pay")
    @ResponseBody
    public Result<Void> pay(@RequestBody HotelOrderInfo hotelOrderInfo) {
        return hotelOrderInfoService.pay(hotelOrderInfo);
    }

    /**
     * 查询用户的民宿订单
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/queryUser")
    @ResponseBody
    public Result<List<HotelOrderInfoVO>> queryUser(@RequestBody HotelOrderInfoQueryDto dto) {
        // 设置上用户ID
        dto.setUserId(LocalThreadHolder.getUserId());
        return hotelOrderInfoService.query(dto);
    }

    /**
     * 统计全站指定日期里面的成交门票金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQueryMoney/{day}")
    @ResponseBody
    public Result<List<ChartVO>> daysQueryMoney(@PathVariable Integer day) {
        return hotelOrderInfoService.daysQueryMoney(day);
    }
    /**
     * 统计成交金额
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQuery/{day}")
    @ResponseBody
    public Result<List<ChartVO>> query(@PathVariable Integer day) {
        return hotelOrderInfoService.daysQuery(day);
    }


}
