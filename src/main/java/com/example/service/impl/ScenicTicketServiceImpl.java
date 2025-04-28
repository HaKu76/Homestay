package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.ScenicTicketMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.dto.query.extend.ScenicTicketQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Scenic;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.vo.ScenicTicketVO;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.VendorVO;
import com.example.service.ScenicTicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点门票的业务逻辑实现类
 */
@Service
public class ScenicTicketServiceImpl implements ScenicTicketService {

    @Resource
    private ScenicTicketMapper scenicTicketMapper;
    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private VendorMapper vendorMapper;

    /**
     * 新增景点门票信息
     *
     * @param scenicTicket 包含要新增的景点门票数据的实体对象，需包含必要业务字段，
     *                     若useStatus字段未设置会自动初始化为不可用状态
     * @return Result<Void> 返回操作结果，成功时不含具体数据内容，
     * 失败时会包含错误信息
     */
    @Override
    public Result<Void> save(ScenicTicket scenicTicket) {
        // 初始化实体创建时间
        scenicTicket.setCreateTime(LocalDateTime.now());

        // 处理状态默认值：当使用状态为空时，强制设置为不可用(false)
        if (scenicTicket.getUseStatus() == null) {
            scenicTicket.setUseStatus(false);
        }

        scenicTicketMapper.save(scenicTicket);
        return ApiResult.success();
    }

    /**
     * 修改景点门票
     * <p>
     * 本方法通过持久层组件更新指定景点门票信息，执行成功后返回通用结果对象
     *
     * @param scenicTicket 包含更新信息的景点门票实体对象，需包含有效主键标识
     *                     （参数校验应由调用方保证）
     * @return Result<Void> 通用返回结果对象，包含操作状态标识：
     * - 成功状态（携带成功状态码）
     * - 失败状态（需通过异常处理机制返回）
     */
    @Override
    public Result<Void> update(ScenicTicket scenicTicket) {
        // 执行持久层更新操作（具体SQL由MyBatis映射文件处理）
        scenicTicketMapper.update(scenicTicket);

        // 构造标准成功响应（无附加数据）
        return ApiResult.success();
    }

    /**
     * 批量删除景点门票记录
     *
     * @param ids 需要删除的景点门票ID集合，包含一个或多个门票唯一标识符。
     *            ID必须存在于数据库中且符合业务约束条件
     * @return Result<Void> 统一响应结果对象，包含操作状态码和提示信息。
     * 成功时返回状态码200和成功消息，失败时包含对应错误状态码和原因说明
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 调用Mapper层执行批量删除操作
        scenicTicketMapper.batchDelete(ids);
        // 构造并返回标准成功响应
        return ApiResult.success();
    }

    /**
     * 查询景点门票列表数据（带分页信息）
     *
     * @param dto 查询条件参数对象，包含分页参数(pageNo/pageSize)和业务查询条件
     * @return Result<List < ScenicTicketVO>> Result包装对象，包含：
     * data: 门票数据列表（根据分页参数截取）
     * total: 符合条件的数据总数（用于分页计算）
     */
    @Override
    public Result<List<ScenicTicketVO>> query(ScenicTicketQueryDto dto) {
        // 查询符合条件的门票总数
        Integer totalCount = scenicTicketMapper.queryCount(dto);
        // 分页查询门票列表数据
        List<ScenicTicketVO> result = scenicTicketMapper.query(dto);
        // 组装分页结果（数据列表+总数）
        return ApiResult.success(result, totalCount);
    }

    /**
     * 查询供应商管理的景点门票信息
     * <p>
     * 实现逻辑：
     * 1. 获取当前登录用户关联的供应商ID
     * 2. 根据供应商ID查询名下所有景点信息
     * 3. 提取景点ID集合
     * 4. 批量查询对应景点的门票信息
     *
     * @return Result<List < ScenicTicketVO>> 包含景点门票信息列表的结果对象，
     * 其中每个元素对应一个景点的门票信息
     */
    @Override
    public Result<List<ScenicTicketVO>> queryVendorTicket() {
        // 获取当前用户绑定的供应商ID（通过安全上下文或会话信息）
        Integer vendorId = getVendorId();

        // 构建景点查询参数：限定当前供应商名下的景点
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setVendorId(vendorId);

        // 执行景点基础信息查询
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 提取所有景点的唯一标识集合（用于批量查询门票）
        List<Integer> scenicIds = scenicVOS.stream()
                .map(Scenic::getId)
                .collect(Collectors.toList());

        // 根据景点ID集合批量查询关联的门票信息
        List<ScenicTicketVO> scenicTicketVOS =
                scenicTicketMapper.queryByScenicIds(scenicIds);

        return ApiResult.success(scenicTicketVOS);
    }

    /**
     * 获取当前登录用户关联的供应商ID
     * <p>
     * 实现逻辑：
     * 1. 通过线程上下文获取当前用户ID
     * 2. 构建供应商查询条件对象
     * 3. 执行供应商查询操作
     * 4. 处理查询结果：当存在关联供应商时返回首个供应商ID，无关联时返回null
     *
     * @return Integer 当前用户关联的供应商ID，返回规则：
     * - 当用户无关联供应商时返回null
     * - 当用户有且仅有一个关联供应商时返回其ID
     * - 理论上不应存在多个关联供应商的情况（需结合业务逻辑保障）
     */
    private Integer getVendorId() {
        // 构建供应商查询条件（基于当前用户上下文）
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());

        // 执行供应商信息查询
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        // 处理查询结果集（根据业务约束应为空或单条记录）
        if (vendorVOS.isEmpty()) {
            return null;
        }
        return vendorVOS.get(0).getId();
    }
}
