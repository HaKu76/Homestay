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
     * 民宿新增
     *
     * @param hotel 民宿实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody Hotel hotel) {
        return hotelService.save(hotel);
    }

    /**
     * 民宿修改
     *
     * @param hotel 民宿实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Hotel hotel) {
        return hotelService.update(hotel);
    }

    /**
     * 民宿批量删除
     *
     * @param ids 民宿ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return hotelService.batchDelete(ids);
    }

    /**
     * 查询民宿
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<HotelVO>> query(@RequestBody HotelQueryDto dto) {
        return hotelService.query(dto);
    }

}
