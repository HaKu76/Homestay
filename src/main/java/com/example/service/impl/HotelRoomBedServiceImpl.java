package com.example.service.impl;

import com.example.mapper.HotelRoomBedMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomBedQueryDto;
import com.example.pojo.entity.HotelRoomBed;
import com.example.service.HotelRoomBedService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 民宿房间床位的业务逻辑实现类
 */
@Service
public class HotelRoomBedServiceImpl implements HotelRoomBedService {

    @Resource
    private HotelRoomBedMapper hotelRoomBedMapper;

    /**
     * 民宿房间床位新增
     *
     * @param hotelRoomBed 民宿房间床位实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(HotelRoomBed hotelRoomBed) {
        //设置创建时间
        hotelRoomBed.setCreateTime(LocalDateTime.now());
        hotelRoomBedMapper.save(hotelRoomBed);
        return ApiResult.success();
    }

    /**
     * 民宿房间床位修改
     *
     * @param hotelRoomBed 民宿房间床位实体
     * @return
     */
    @Override
    public Result<Void> update(HotelRoomBed hotelRoomBed) {
        hotelRoomBedMapper.update(hotelRoomBed);
        return ApiResult.success();
    }

    /**
     * 民宿房间床位删除
     *
     * @param ids 民宿房间床位ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        hotelRoomBedMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 民宿房间床位查询
     *
     * @param dto 查询实体
     * @return Result<List < HotelRoomBed>>
     */
    @Override
    public Result<List<HotelRoomBed>> query(HotelRoomBedQueryDto dto) {
        Integer totalCount = hotelRoomBedMapper.queryCount(dto);
        List<HotelRoomBed> result = hotelRoomBedMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
