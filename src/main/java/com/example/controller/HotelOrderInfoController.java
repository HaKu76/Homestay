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
     * 新增民宿订单
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return Result<Void> 响应结果
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody HotelOrderInfo hotelOrderInfo) {
        // 调用业务层新增民宿订单
        return hotelOrderInfoService.save(hotelOrderInfo);
    }

    /**
     * 修改民宿订单
     *
     * @param hotelOrderInfo 民宿订单实体
     * @return Result<Void> 响应结果
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody HotelOrderInfo hotelOrderInfo) {
        // 调用业务层修改民宿订单
        return hotelOrderInfoService.update(hotelOrderInfo);
    }

    /**
     * 批量删除民宿订单
     *
     * @param ids 民宿订单ID列表
     * @return Result<Void> 响应结果
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        // 调用业务层批量删除民宿订单
        return hotelOrderInfoService.batchDelete(ids);
    }

    /**
     * 查询民宿订单
     *
     * @param dto 查询参数
     * @return Result<List < HotelOrderInfoVO>> 响应结果
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
     * @param dto 查询参数
     * @return Result<List < HotelOrderInfoVO>> 响应结果
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
     * @param hotelOrderInfo 参数
     * @return Result<Void> 响应结果
     */
    @Pager
    @PostMapping(value = "/pay")
    @ResponseBody
    public Result<Void> pay(@RequestBody HotelOrderInfo hotelOrderInfo) {
        return hotelOrderInfoService.pay(hotelOrderInfo);
    }

    /**
     * 查询用户民宿订单
     *
     * @param dto 查询参数
     * @return Result<List < HotelOrderInfoVO>> 响应结果
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
     * @param day 天数
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
     * @param day 天数
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQuery/{day}")
    @ResponseBody
    public Result<List<ChartVO>> query(@PathVariable Integer day) {
        return hotelOrderInfoService.daysQuery(day);
    }

}