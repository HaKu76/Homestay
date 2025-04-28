package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomBedQueryDto;
import com.example.pojo.entity.HotelRoomBed;
import com.example.service.HotelRoomBedService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 民宿房间床位控制器
 */
@RestController
@RequestMapping("/hotelRoomBed")
public class HotelRoomBedController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private HotelRoomBedService hotelRoomBedService;

    /**
     * 新增民宿房间床位
     *
     * @param hotelRoomBed 民宿房间床位实体
     * @return Result<Void> 响应结果
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody HotelRoomBed hotelRoomBed) {
        return hotelRoomBedService.save(hotelRoomBed);
    }

    /**
     * 修改民宿房间床位
     *
     * @param hotelRoomBed 民宿房间床位实体
     * @return Result<Void> 响应结果
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody HotelRoomBed hotelRoomBed) {
        return hotelRoomBedService.update(hotelRoomBed);
    }

    /**
     * 批量删除民宿房间床位
     *
     * @param ids 民宿房间床位ID列表
     * @return Result<Void> 响应结果
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return hotelRoomBedService.batchDelete(ids);
    }

    /**
     * 查询民宿房间床位
     *
     * @param dto 查询条件
     * @return Result<List < HotelRoomBed>> 响应结果
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<HotelRoomBed>> query(@RequestBody HotelRoomBedQueryDto dto) {
        return hotelRoomBedService.query(dto);
    }

}
