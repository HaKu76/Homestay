package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelOrderInfoQueryDto;
import com.example.pojo.entity.HotelOrderInfo;
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

}
