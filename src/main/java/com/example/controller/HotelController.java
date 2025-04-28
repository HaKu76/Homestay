package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.vo.HotelVO;
import com.example.service.HotelService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 民宿控制器
 */
@RestController
@RequestMapping("/hotel")
public class HotelController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private HotelService hotelService;

    /**
     * 新增民宿
     *
     * @param hotel 民宿实体
     * @return
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody Hotel hotel) {
        // 调用业务层新增民宿
        return hotelService.save(hotel);
    }

    /**
     * 民宿修改
     *
     * @param hotel 民宿实体
     * @return
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Hotel hotel) {
        // 调用业务层修改民宿
        return hotelService.update(hotel);
    }

    /**
     * 批量删除民宿
     *
     * @param ids 民宿id集合
     * @return
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        // 调用业务层批量删除民宿
        return hotelService.batchDelete(ids);
    }

    /**
     * 查询供应商名下的民宿信息
     *
     * @param dto 查询参数
     * @return
     */
    @Pager
    @PostMapping(value = "/queryVendorHotel")
    @ResponseBody
    public Result<List<HotelVO>> queryVendorHotel(@RequestBody HotelQueryDto dto) {
        // 调用业务层查询供应商名下的民宿信息
        return hotelService.queryVendorHotel(dto);
    }

    /**
     * 查询民宿信息
     *
     * @param dto 查询参数
     * @return
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<HotelVO>> query(@RequestBody HotelQueryDto dto) {
        // 调用业务层查询民宿信息
        return hotelService.query(dto);
    }
}
