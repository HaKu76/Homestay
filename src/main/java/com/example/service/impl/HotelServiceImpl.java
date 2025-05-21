package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.HotelMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.vo.HotelVO;
import com.example.pojo.vo.VendorVO;
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
    @Resource
    private VendorMapper vendorMapper;

    /**
     * 新增民宿信息
     * 
     * 自动设置创建时间和供应商ID后执行持久化操作
     *
     * @param hotel 民宿实体对象，包含需要新增的民宿详细信息（不可为空）
     * @return Result<Void> 操作结果对象，包含成功状态和空数据体（通过ApiResult.success()生成）
     */
    @Override
    public Result<Void> save(Hotel hotel) {
        // 初始化基础字段：创建时间和当前供应商ID
        hotel.setCreateTime(LocalDateTime.now());
        hotel.setVendorId(getVendorId());

        hotelMapper.save(hotel);
        return ApiResult.success();
    }

    /**
     * 修改民宿信息
     *
     * @param hotel 待修改的民宿实体对象，包含更新后的字段信息
     * @return Result<Void> 操作结果，包含状态码和提示信息（Void表示无具体数据返回）
     */
    @Override
    public Result<Void> update(Hotel hotel) {
        // 执行数据库更新操作
        hotelMapper.update(hotel);
        // 返回封装的成功响应
        return ApiResult.success();
    }

    /**
     * 批量删除民宿信息
     * 根据民宿ID列表进行物理删除操作，该操作不可逆
     *
     * @param ids 民宿ID列表，要求非空且至少包含一个有效ID
     * @return Result<Void> 统一响应结果，成功时返回操作成功的Result对象
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行批量删除操作（基于MyBatis的批量更新机制）
        hotelMapper.batchDelete(ids);

        // 返回标准化成功响应（包含成功状态码和默认消息）
        return ApiResult.success();
    }

    /**
     * 分页查询民宿信息
     *
     * @param dto 查询参数实体，包含分页参数（pageNo/pageSize）、
     *            筛选条件（如区域/价格区间/设施要求等）、
     *            排序字段等查询条件
     * @return Result<List < HotelVO>> 包含分页查询结果的对象，
     * 其中data为当前页的民宿数据集合，
     * total为符合条件的数据总条数（用于分页计算）
     */
    @Override
    public Result<List<HotelVO>> query(HotelQueryDto dto) {
        // 获取符合条件的数据总量（不包含分页限制）
        Integer totalCount = hotelMapper.queryCount(dto);

        // 执行分页查询获取当前页数据集合
        List<HotelVO> result = hotelMapper.query(dto);

        // 封装分页结果对象（数据集合+总条数）
        return ApiResult.success(result, totalCount);
    }

    /**
     * 查询供应商名下维护的民宿信息
     *
     * @param dto 酒店查询数据传输对象，包含分页参数、过滤条件等查询参数。
     *            方法内部会自动注入当前供应商ID到dto中作为查询条件
     * @return Result<List < HotelVO>> 包含酒店信息列表的通用响应结果对象，数据部分为符合查询条件的酒店VO集合。
     * 其中：
     * - code: 响应状态码
     * - message: 响应消息
     * - data: 实际数据（酒店信息列表）
     */
    @Override
    public Result<List<HotelVO>> queryVendorHotel(HotelQueryDto dto) {
        // 获取当前认证供应商ID并注入查询条件
        Integer vendorId = getVendorId();
        dto.setVendorId(vendorId);

        // 执行带供应商ID的通用查询
        return query(dto);
    }

    /**
     * 获取当前用户关联的供应商ID
     * 
     * 实现逻辑：
     * 1. 根据当前线程持有的用户ID构建供应商查询条件
     * 2. 执行供应商查询
     * 3. 处理查询结果：
     * - 空集合返回null（表示未关联供应商）
     * - 非空集合返回首条记录的ID（根据业务约定应只有单个供应商）
     *
     * @return 关联的供应商ID，当不存在关联供应商时返回null
     */
    private Integer getVendorId() {
        // 构建供应商查询对象
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        // 设置查询条件的用户ID为当前线程上下文中的用户ID
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());

        // 执行供应商查询，获取符合条件的供应商列表
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        /*
         * 处理查询结果：
         * 1. 空列表表示用户未关联供应商
         * 2. 根据业务规则预期最多存在一个供应商关联
         */
        if (vendorVOS.isEmpty()) {
            return null;
        }
        return vendorVOS.get(0).getId();
    }
}