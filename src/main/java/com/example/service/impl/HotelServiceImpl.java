package com.example.service.impl;

import com.example.mapper.HotelMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.vo.HotelVO;
import com.example.service.HotelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 民宿的业务逻辑实现类
 */
@Service
public class HotelServiceImpl implements HotelService {

    @Resource
    private HotelMapper hotelMapper;

    /**
     * 景点新增
     *
     * @param hotel 民宿实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Hotel hotel) {
        //设置创建时间
        hotel.setCreateTime(LocalDateTime.now());
        hotelMapper.save(hotel);
        return ApiResult.success();
    }

    /**
     * 景点修改
     *
     * @param hotel 景点实体
     * @return
     */
    @Override
    public Result<Void> update(Hotel hotel) {
        hotelMapper.update(hotel);
        return ApiResult.success();
    }

    /**
     * 景点删除
     *
     * @param ids 景点ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        hotelMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<HotelVO>> query(HotelQueryDto dto) {
        Integer totalCount = hotelMapper.queryCount(dto);
        List<HotelVO> result = hotelMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }
}
