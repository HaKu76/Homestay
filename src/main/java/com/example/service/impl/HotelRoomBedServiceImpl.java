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
     * 新增民宿房间床位实体到数据库
     *
     * @param hotelRoomBed 待持久化的民宿房间床位实体对象，需要包含房间ID、床位类型等必要字段，
     *                     创建时间字段会被自动填充不需要传入
     * @return Result<Void> 包含操作结果的响应对象，
     * 成功时返回状态码200和成功消息，失败时包含对应的错误信息
     */
    @Override
    public Result<Void> save(HotelRoomBed hotelRoomBed) {
        // 设置实体创建时间（系统当前时间）
        hotelRoomBed.setCreateTime(LocalDateTime.now());

        // 执行数据库持久化操作
        hotelRoomBedMapper.save(hotelRoomBed);

        return ApiResult.success();
    }

    /**
     * 修改民宿房间床位信息
     *
     * @param hotelRoomBed 包含更新字段的床位实体对象，需包含主键id用于定位记录
     *                     - id：必填，待修改床位记录的唯一标识
     *                     - 其他字段：需要更新的床位属性值
     * @return Result<Void> 操作结果包装对象
     * - 成功：返回带有成功状态码和消息的Result
     * - 失败：返回带有错误码和异常信息的Result
     */
    @Override
    public Result<Void> update(HotelRoomBed hotelRoomBed) {
        // 执行MyBatis更新操作
        hotelRoomBedMapper.update(hotelRoomBed);

        // 封装标准成功响应
        return ApiResult.success();
    }

    /**
     * 批量删除民宿房间床位信息
     *
     * @param ids 待删除的床位ID列表，列表不能为空且每个ID必须为有效正整数
     * @return 统一响应结果对象，成功时返回状态码200和空数据体
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 调用Mapper层执行批量删除操作
        hotelRoomBedMapper.batchDelete(ids);
        // 构造标准成功响应
        return ApiResult.success();
    }

    /**
     * 查询民宿房间床位信息列表（带分页）
     *
     * @param dto 查询条件参数对象，包含分页参数及过滤条件。字段说明：
     *            - pageNum：页码（非必填，默认1）
     *            - pageSize：每页条数（非必填，默认10）
     *            - roomId：必填参数，关联的房间ID
     *            - bedType：床型过滤条件（可选）
     * @return 包含分页数据的响应对象，结构说明：
     * - code：响应状态码（200表示成功）
     * - msg：操作结果描述
     * - data：当前页的床位数据集合
     * - total：总记录数（用于分页计算）
     */
    @Override
    public Result<List<HotelRoomBed>> query(HotelRoomBedQueryDto dto) {
        // 执行总数查询和分页数据查询
        Integer totalCount = hotelRoomBedMapper.queryCount(dto);
        List<HotelRoomBed> result = hotelRoomBedMapper.query(dto);

        // 封装分页响应对象（自动计算总页数）
        return ApiResult.success(result, totalCount);
    }
}
