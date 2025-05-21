package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.ScenicTicketMapper;
import com.example.mapper.ScenicTicketOrderMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.dto.query.extend.*;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.*;
import com.example.service.ScenicTicketOrderService;
import com.example.utils.DateUtil;
import com.example.utils.DecimalUtils;
import com.example.utils.MoneyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点门票订单的业务逻辑实现类
 */
@Service
public class ScenicTicketOrderServiceImpl implements ScenicTicketOrderService {

    @Resource
    private ScenicTicketOrderMapper scenicTicketOrderMapper;
    @Resource
    private VendorMapper vendorMapper;
    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private ScenicTicketMapper scenicTicketMapper;

    /**
     * 新增景点门票订单
     * 
     * 该方法包含以下业务逻辑：
     * 1. 基础参数校验（联系人信息）
     * 2. 门票信息有效性验证（库存、可用状态）
     * 3. 订单金额计算（支持折扣计算）
     * 4. 订单信息初始化（用户ID、时间、状态）
     * 5. 库存扣减操作
     *
     * @param scenicTicketOrder 景点门票订单实体对象，包含联系人信息、购买数量等订单详情
     * @return Result<Void> 操作结果对象，包含成功状态或错误信息：
     * - 参数校验失败返回错误信息
     * - 门票不可用/库存不足返回错误信息
     * - 下单成功返回操作成功标识
     */
    @Override
    public Result<Void> save(ScenicTicketOrder scenicTicketOrder) {
        // 基础参数校验：联系人和联系电话
        if (!StringUtils.hasText(scenicTicketOrder.getContactPerson()) ||
                !StringUtils.hasText(scenicTicketOrder.getContactPhone())) {
            return ApiResult.error("联系人或联系电话不为空");
        }

        // 门票信息查询及有效性验证
        ScenicTicketQueryDto scenicTicketQueryDto = new ScenicTicketQueryDto();
        scenicTicketQueryDto.setId(scenicTicketOrder.getTicketId());
        List<ScenicTicketVO> scenicTicketVOS = scenicTicketMapper.query(scenicTicketQueryDto);
        if (scenicTicketVOS.isEmpty()) {
            return ApiResult.error("暂无门票信息");
        }

        // 门票状态检查
        ScenicTicketVO scenicTicketVO = scenicTicketVOS.get(0);
        if (!scenicTicketVO.getUseStatus()) {
            return ApiResult.error("门票暂不可用");
        }

        // 库存检查
        if (scenicTicketOrder.getBuyNumber() > scenicTicketVO.getNumber()) {
            return ApiResult.error("门票库存不足");
        }

        // 订单金额计算（含折扣处理）
        BigDecimal amount = DecimalUtils.calculateTotal(
                scenicTicketOrder.getBuyNumber(),
                scenicTicketVO.getPrice(),
                scenicTicketVO.getDiscount() == null ? 1 : (scenicTicketVO.getDiscount() / 10)
        );
        scenicTicketOrder.setAmount(amount);

        // 订单信息初始化
        scenicTicketOrder.setUserId(LocalThreadHolder.getUserId());
        scenicTicketOrder.setCreateTime(LocalDateTime.now());
        scenicTicketOrder.setTicketId(scenicTicketVO.getId());
        scenicTicketOrder.setPayStatus(false);

        // 持久化操作
        scenicTicketOrderMapper.save(scenicTicketOrder);

        // 库存更新
        ScenicTicket scenicTicket = new ScenicTicket();
        scenicTicket.setId(scenicTicketVO.getId());
        scenicTicket.setNumber(scenicTicketVO.getNumber() - scenicTicketOrder.getBuyNumber());
        scenicTicketMapper.update(scenicTicket);

        return ApiResult.success("下单成功");
    }

    /**
     * 修改景点门票订单信息
     *
     * @param scenicTicketOrder 待修改的景点门票订单实体对象，包含订单的新数据和标识字段
     * @return Result<Void> 返回操作结果，包含成功/失败状态及提示信息
     */
    @Override
    public Result<Void> update(ScenicTicketOrder scenicTicketOrder) {
        // 执行数据库更新操作
        scenicTicketOrderMapper.update(scenicTicketOrder);
        return ApiResult.success();
    }

    /**
     * 批量删除景点门票订单
     *
     * @param ids 需要删除的订单ID集合，包含一个或多个订单唯一标识
     *            要求：
     *            - 列表元素应为有效存在的订单ID
     *            - 列表不能为空且元素数量不超过系统限制
     * @return Result<Void> 统一响应结果对象
     * 成功时：
     * - 返回状态码200和成功提示
     * 失败时：
     * - 返回对应错误码（如：参数校验失败400，数据不存在404等）
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 通过Mapper层执行批量删除操作
        scenicTicketOrderMapper.batchDelete(ids);

        // 返回标准化成功响应
        return ApiResult.success();
    }

    /**
     * 查询景点门票订单
     *
     * @param dto 包含查询条件的传输对象，包含以下典型参数：
     *            - userId 用户ID（可选）
     *            - orderStatus 订单状态（可选）
     *            - scenicId 景点ID（可选）
     *            - startTime 订单创建时间范围-开始（可选）
     *            - endTime 订单创建时间范围-结束（可选）
     * @return Result<List < ScenicTicketOrderVO>> 包含分页查询结果的响应对象，其中：
     * - data 为符合查询条件的订单数据列表
     * - total 为符合条件的所有记录总数（用于分页展示）
     */
    @Override
    public Result<List<ScenicTicketOrderVO>> query(ScenicTicketOrderQueryDto dto) {
        // 查询符合条件的订单总数（不包含分页参数）
        Integer totalCount = scenicTicketOrderMapper.queryCount(dto);

        // 执行分页查询（包含分页参数），获取当前页数据列表
        List<ScenicTicketOrderVO> result = scenicTicketOrderMapper.query(dto);

        return ApiResult.success(result, totalCount);
    }

    /**
     * 查询供应商管理的全部景点门票订单（包含分页数据）
     *
     * @param dto 订单查询参数对象，包含分页参数和过滤条件
     * @return Result<List < ScenicTicketOrderVO>> 包含订单列表和总数量的分页结果对象，当供应商无效时返回空列表
     * totalCount为总数量
     */
    @Override
    public Result<List<ScenicTicketOrderVO>> queryScenicTicketOrder(ScenicTicketOrderQueryDto dto) {
        // 供应商身份有效性检查：获取当前登录供应商ID
        Integer vendorId = getVendorId();
        if (vendorId == null) {
            return ApiResult.success(new ArrayList<>());
        }

        // 查询该供应商管理的所有景点信息
        ScenicQueryDto queryDto = new ScenicQueryDto();
        queryDto.setVendorId(vendorId);
        List<ScenicVO> scenicVOS = scenicMapper.query(queryDto);

        // 提取景点ID集合用于后续查询
        List<Integer> scenicIds = scenicVOS.stream()
                .map(ScenicVO::getId)
                .collect(Collectors.toList());

        // 根据景点ID集合查询对应的门票信息
        List<ScenicTicketVO> scenicTicketVOS = scenicTicketMapper.queryByScenicIds(scenicIds);

        // 提取门票ID集合用于订单查询
        List<Integer> scenicTicketIds = scenicTicketVOS.stream()
                .map(ScenicTicketVO::getId)
                .collect(Collectors.toList());

        // 构建订单查询参数（包含门票ID集合和前端参数）
        ScenicTicketQueryParamDto orderQueryParam = new ScenicTicketQueryParamDto(scenicTicketIds, dto);

        // 执行分页查询：订单数据和总数量
        List<ScenicTicketOrderVO> scenicTicketOrderVOS = scenicTicketOrderMapper.queryByScenicIds(orderQueryParam);
        Integer totalCount = scenicTicketOrderMapper.queryCountByScenicIds(orderQueryParam);

        // 封装分页结果返回
        return ApiResult.success(scenicTicketOrderVOS, totalCount);
    }

    /**
     * 统计近期成交金额并生成图表数据
     *
     * @param day 查询天数范围（统计当前时间往前推day天的数据）
     * @return 封装结果对象，包含按日统计的图表数据集合。每个ChartVO对应一天的成交统计
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        // 获取查询时间范围（包含开始时间和结束时间的封装对象）
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建景区门票订单查询条件
        ScenicTicketOrderQueryDto dto = new ScenicTicketOrderQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());

        // 调用远程服务查询景区门票订单数据
        Result<List<ScenicTicketOrderVO>> scenicTicketOrder = queryScenicTicketOrder(dto);
        ApiResult<List<ScenicTicketOrderVO>> response = (ApiResult) scenicTicketOrder;
        List<ScenicTicketOrderVO> data = response.getData();

        // 转换数据结构：将订单数据转换为金额统计专用DTO（包含金额和支付时间）
        List<MoneyDto> moneyDtoList = data.stream().map(scenicTicketOrderVO -> new MoneyDto(
                scenicTicketOrderVO.getAmount(),
                scenicTicketOrderVO.getPayTime()
        )).collect(Collectors.toList());

        // 生成每日销售报表（按自然日分组统计）
        List<ChartVO> chartVOS = MoneyUtils.generateDailySalesReport(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 查询当前用户的指定天数内门票消费金额数据
     * 
     * 通过时间范围构建查询参数，获取用户门票订单记录后转换为金额时序数据，
     * 最终生成按日统计的消费金额图表数据
     *
     * @param day 需要查询的历史天数范围（例如：7表示最近7天）
     * @return 包含图表数据集合的标准响应对象，数据按自然日分组统计
     */
    @Override
    public Result<List<ChartVO>> daysQueryUser(Integer day) {
        // 生成指定天数的起止时间范围
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建带用户维度的订单查询参数
        ScenicTicketOrderQueryDto dto = new ScenicTicketOrderQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());
        dto.setUserId(LocalThreadHolder.getUserId());

        // 执行订单数据查询
        List<ScenicTicketOrderVO> scenicTicketOrderVOS = scenicTicketOrderMapper.query(dto);

        // 转换原始数据为金额时序结构
        List<MoneyDto> moneyDtoList = scenicTicketOrderVOS.stream()
                .map(scenicTicketOrderVO -> new MoneyDto(
                        scenicTicketOrderVO.getAmount(),
                        scenicTicketOrderVO.getPayTime())).collect(Collectors.toList());

        // 生成每日消费金额统计报表
        List<ChartVO> chartVOS = MoneyUtils.generateDailySalesReport(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 统计全站指定日期范围内的成交门票金额
     *
     * @param day 统计天数，单位为天（如：7表示最近7天）
     * @return Result<List < ChartVO>> 包含每日销售额图表的响应结果，ChartVO中包含日期和对应销售额
     */
    @Override
    public Result<List<ChartVO>> daysQueryMoney(Integer day) {
        // 获取时间范围工具类处理
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建订单查询条件
        ScenicTicketOrderQueryDto dto = new ScenicTicketOrderQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());

        // 执行订单数据查询
        List<ScenicTicketOrderVO> scenicTicketOrderVOS = scenicTicketOrderMapper.query(dto);

        // 转换数据结构：订单数据 -> 金额数据
        List<MoneyDto> moneyDtoList = scenicTicketOrderVOS.stream()
                .map(scenicTicketOrderVO -> new MoneyDto(
                        scenicTicketOrderVO.getAmount(),
                        scenicTicketOrderVO.getPayTime())).collect(Collectors.toList());

        // 生成每日销售报表
        List<ChartVO> chartVOS = MoneyUtils.generateDailySalesReport(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 获取当前用户关联的供应商ID
     * 
     * 方法逻辑说明：
     * 1. 构建供应商查询条件，设置当前操作用户ID
     * 2. 执行供应商查询操作
     * 3. 根据查询结果返回供应商ID：当存在供应商时返回首条记录的ID，无供应商时返回null
     *
     * @return 关联的供应商ID，可能返回以下值：
     * - 非空Integer：当存在唯一关联供应商时
     * - null：当用户未关联任何供应商时
     */
    private Integer getVendorId() {
        // 构建供应商查询条件对象
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        // 设置当前登录用户ID作为查询条件
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());

        // 执行供应商数据查询
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        // 处理查询结果集
        if (CollectionUtils.isEmpty(vendorVOS)) {
            return null;
        }
        // 返回首个供应商ID（根据业务规则，用户最多关联一个供应商）
        return vendorVOS.get(0).getId();
    }

    /**
     * 门票订单支付处理
     *
     * @param scenicTicketOrder 待支付的门票订单对象，需要包含订单基本数据和业务标识。
     *                          方法将直接修改该对象的支付状态和支付时间字段
     * @return Result<Void> 返回支付操作结果，包含成功标识和错误信息（如适用）。
     * 成功时data字段为空，仅通过code标识操作状态
     */
    @Override
    public Result<Void> pay(ScenicTicketOrder scenicTicketOrder) {
        // 更新订单支付状态和支付时间
        scenicTicketOrder.setPayStatus(true);
        scenicTicketOrder.setPayTime(LocalDateTime.now());

        // 执行数据库持久化操作
        scenicTicketOrderMapper.update(scenicTicketOrder);

        return ApiResult.success();
    }

}
