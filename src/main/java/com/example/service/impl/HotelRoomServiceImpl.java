package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.HotelMapper;
import com.example.mapper.HotelRoomMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.dto.query.extend.HotelRoomQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.entity.HotelRoom;
import com.example.pojo.vo.HotelRoomVO;
import com.example.pojo.vo.HotelVO;
import com.example.pojo.vo.VendorVO;
import com.example.service.HotelRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 民宿的业务逻辑实现类
 */
@Service
public class HotelRoomServiceImpl implements HotelRoomService {

    @Resource
    private HotelRoomMapper hotelRoomMapper;
    @Resource
    private VendorMapper vendorMapper;
    @Resource
    private HotelMapper hotelMapper;

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

    /**
     * 民宿房间查询 -- 做了权限隔离的
     *
     * @param dto 查询实体
     * @return Result<HotelRoomVO>
     */
    @Override
    public Result<List<HotelRoomVO>> queryVendorRoom(HotelRoomQueryDto dto) {
        // 1. 先查供应商信息
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 两种情况：第一种是没有申请；第二种是已经申请了但是没有审核。
        // 未申请
        if (vendorVOS.isEmpty()) {
            return ApiResult.success();
        }
        VendorVO vendorVO = vendorVOS.get(0);
        // 未审核
        if (!vendorVO.getIsAudit()) {
            return ApiResult.success();
        }
        Integer vendorId = vendorVO.getId(); // 供应商ID
        // 2. 通过供应商ID查询所管辖的民宿
        HotelQueryDto hotelQueryDto = new HotelQueryDto();
        hotelQueryDto.setVendorId(vendorId);
        List<HotelVO> hotelVOS = hotelMapper.query(hotelQueryDto);
        // 如果发现供应商账户下并没有管理有民宿，直接退出
        if (hotelVOS.isEmpty()) {
            return ApiResult.success();
        }
        // 通过stream流，取出民宿ID集合
        List<Integer> hotelIds = hotelVOS.stream()
                .map(Hotel::getId)
                .collect(Collectors.toList());
        dto.setHotelIds(hotelIds);
        Integer totalCount = hotelRoomMapper.queryVendorRoomCount(dto);
        List<HotelRoomVO> result = hotelRoomMapper.queryVendorRoom(dto);
        return ApiResult.success(result, totalCount);
    }
}
