package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.entity.HotelRoom;
import com.example.pojo.vo.HotelRoomVO;
import com.example.service.HotelRoomService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 民宿房间控制器
 */
@RestController
@RequestMapping("/hotelRoom")
public class HotelRoomController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private HotelRoomService hotelRoomService;

    /**
     * 民宿房间新增
     *
     * @param hotelRoom 民宿房间实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody HotelRoom hotelRoom) {
        return hotelRoomService.save(hotelRoom);
    }

    /**
     * 民宿房间修改
     *
     * @param hotelRoom 民宿房间实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody HotelRoom hotelRoom) {
        return hotelRoomService.update(hotelRoom);
    }

    /**
     * 民宿房间批量删除
     *
     * @param ids 民宿房间ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return hotelRoomService.batchDelete(ids);
    }

    /**
     * 查询民宿房间
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<HotelRoomVO>> query(@RequestBody HotelRoomQueryDto dto) {
        return hotelRoomService.query(dto);
    }

    /**
     * 查询酒店房间 -- 做了权限隔离的
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/queryVendorRoom")
    @ResponseBody
    public Result<List<HotelRoomVO>> queryVendorRoom(@RequestBody HotelRoomQueryDto dto) {
        return hotelRoomService.queryVendorRoom(dto);
    }
}
