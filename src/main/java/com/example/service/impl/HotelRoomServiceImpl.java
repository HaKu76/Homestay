package com.example.service.impl;

import com.example.mapper.HotelRoomMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.entity.HotelRoom;
import com.example.pojo.vo.HotelRoomVO;
import com.example.service.HotelRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 民宿的业务逻辑实现类
 */
@Service
public class HotelRoomServiceImpl implements HotelRoomService {

    @Resource
    private HotelRoomMapper hotelRoomMapper;

    /**
     * 民宿房间新增
     *
     * @param hotelRoom 民宿实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(HotelRoom hotelRoom) {
        //设置创建时间
        hotelRoom.setCreateTime(LocalDateTime.now());
        hotelRoomMapper.save(hotelRoom);
        return ApiResult.success();
    }

    /**
     * 民宿房间修改
     *
     * @param hotelRoom 民宿房间实体
     * @return
     */
    @Override
    public Result<Void> update(HotelRoom hotelRoom) {
        hotelRoomMapper.update(hotelRoom);
        return ApiResult.success();
    }

    /**
     * 民宿房间删除
     *
     * @param ids 民宿房间ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        hotelRoomMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 民宿房间查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<HotelRoomVO>> query(HotelRoomQueryDto dto) {
        Integer totalCount = hotelRoomMapper.queryCount(dto);
        List<HotelRoomVO> result = hotelRoomMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
