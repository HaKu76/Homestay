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
     * 新增民宿房间实体到数据库
     *
     * @param hotelRoom 需要新增的民宿房间实体对象，包含房间基础信息。调用前需确保必要字段已填充，不能为null
     * @return 操作结果封装对象，包含状态码和消息。成功时返回的Result对象data字段为null，
     * 失败时会通过错误码和消息说明具体原因
     */
    @Override
    public Result<Void> save(HotelRoom hotelRoom) {
        // 自动填充创建时间
        hotelRoom.setCreateTime(LocalDateTime.now());
        hotelRoomMapper.save(hotelRoom);
        return ApiResult.success();
    }

    /**
     * 修改民宿房间信息
     * <p>
     * 根据传入的民宿房间实体更新数据库记录，要求房间ID必须存在且为有效值
     *
     * @param hotelRoom 包含更新字段的民宿房间实体对象，非空校验由调用方保证
     * @return 统一响应结果对象，Void表示无具体返回值但包含状态码和消息
     */
    @Override
    public Result<Void> update(HotelRoom hotelRoom) {
        // 执行数据库更新操作，受影响行数由框架自动处理
        hotelRoomMapper.update(hotelRoom);
        return ApiResult.success();
    }

    /**
     * 批量删除民宿房间信息
     *
     * @param ids 需要删除的民宿房间ID集合，不可为空列表且ID必须有效
     * @return Result<Void> 统一响应结果对象，包含操作成功状态：
     * - 成功时返回状态码200和成功消息
     * - 失败时会通过异常处理机制返回对应错误信息
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 调用Mapper层执行批量删除操作
        hotelRoomMapper.batchDelete(ids);
        // 返回标准成功响应（默认状态码200）
        return ApiResult.success();
    }

    /**
     * 查询民宿房间列表
     *
     * @param dto 查询参数对象，包含分页参数、房型状态、时间范围等过滤条件
     * @return Result<List < HotelRoomVO>> 包含分页查询结果的响应对象，
     * 其中data字段为当前页民宿房间数据集合，
     * total字段为符合条件的数据总条数
     */
    @Override
    public Result<List<HotelRoomVO>> query(HotelRoomQueryDto dto) {
        // 获取符合查询条件的总记录数（用于前端分页计算）
        Integer totalCount = hotelRoomMapper.queryCount(dto);

        // 执行分页数据查询，返回当前页数据集合
        List<HotelRoomVO> result = hotelRoomMapper.query(dto);

        // 封装包含总数和分页数据的标准响应格式
        return ApiResult.success(result, totalCount);
    }

    /**
     * 查询供应商名下的民宿房间（带权限隔离）
     * <p>
     * 实现逻辑：
     * 1. 基于当前登录用户查询关联的供应商信息
     * 2. 校验供应商资质（未申请/未审核状态直接返回空结果）
     * 3. 通过有效供应商查询关联民宿
     * 4. 基于民宿集合执行分页房间查询
     *
     * @param dto 房间查询参数对象，包含分页、过滤条件等信息。执行过程中会自动注入hotelIds字段
     * @return Result<List < HotelRoomVO>> 包含分页结果的响应对象，data为房间数据列表，totalCount为符合条件的总记录数
     */
    @Override
    public Result<List<HotelRoomVO>> queryVendorRoom(HotelRoomQueryDto dto) {
        // 查询当前用户关联的供应商信息（基于ThreadLocal存储的用户ID）
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        /* 供应商状态检查：未申请或未审核时返回空结果 */
        if (vendorVOS.isEmpty()) {
            return ApiResult.success();
        }
        VendorVO vendorVO = vendorVOS.get(0);
        if (!vendorVO.getIsAudit()) {
            return ApiResult.success();
        }

        /* 获取供应商关联的民宿集合 */
        Integer vendorId = vendorVO.getId();
        HotelQueryDto hotelQueryDto = new HotelQueryDto();
        hotelQueryDto.setVendorId(vendorId);
        List<HotelVO> hotelVOS = hotelMapper.query(hotelQueryDto);

        /* 无关联民宿时提前返回 */
        if (hotelVOS.isEmpty()) {
            return ApiResult.success();
        }

        /* 提取民宿ID集合用于房间查询 */
        List<Integer> hotelIds = hotelVOS.stream()
                .map(Hotel::getId)
                .collect(Collectors.toList());
        dto.setHotelIds(hotelIds);

        /* 执行分页查询并返回结果 */
        Integer totalCount = hotelRoomMapper.queryVendorRoomCount(dto);
        List<HotelRoomVO> result = hotelRoomMapper.queryVendorRoom(dto);
        return ApiResult.success(result, totalCount);
    }
}
