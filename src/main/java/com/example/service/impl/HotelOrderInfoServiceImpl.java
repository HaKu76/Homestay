package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.HotelMapper;
import com.example.mapper.HotelOrderInfoMapper;
import com.example.mapper.HotelRoomMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.dto.query.extend.*;
import com.example.pojo.entity.HotelOrderInfo;
import com.example.pojo.vo.*;
import com.example.service.HotelOrderInfoService;
import com.example.utils.DateUtil;
import com.example.utils.DecimalUtils;
import com.example.utils.MoneyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 民宿订单的业务逻辑实现类
 */
@Service
public class HotelOrderInfoServiceImpl implements HotelOrderInfoService {

    @Resource
    private HotelOrderInfoMapper hotelOrderInfoMapper;
    @Resource
    private VendorMapper vendorMapper;
    @Resource
    private HotelMapper hotelMapper;
    @Resource
    private HotelRoomMapper hotelRoomMapper;

    /**
     * 新增民宿订单
     * 
     * 实现逻辑：
     * 1. 校验房间有效性
     * 2. 防止重复预定（24小时内同房间只能预定一次）
     * 3. 计算订单金额并填充必要字段
     * 4. 持久化订单数据
     *
     * @param hotelOrderInfo 民宿订单实体，需包含房间ID、用户信息等必要字段
     * @return Result<Void> 操作结果，成功时返回空数据，失败时返回错误信息
     */
    @Override
    public Result<Void> save(HotelOrderInfo hotelOrderInfo) {
        // 校验房间有效性
        HotelRoomQueryDto hotelRoomQueryDto = new HotelRoomQueryDto();
        hotelRoomQueryDto.setId(hotelOrderInfo.getRoomId());
        List<HotelRoomVO> hotelRoomVOS = hotelRoomMapper.query(hotelRoomQueryDto);
        if (hotelRoomVOS.isEmpty()) {
            return ApiResult.error("房间信息未找到");
        }

        // 防止重复下单：查询最近订单并校验时间间隔
        HotelOrderInfoQueryDto queryDto = new HotelOrderInfoQueryDto();
        queryDto.setRoomId(hotelOrderInfo.getRoomId());
        queryDto.setCurrent(1);
        queryDto.setSize(1);
        List<HotelOrderInfoVO> orderInfoVOS = hotelOrderInfoMapper.query(queryDto);
        if (!orderInfoVOS.isEmpty()) {
            HotelOrderInfoVO hotelOrderInfoVO = orderInfoVOS.get(0);
            // 计算与最近订单的时间差（天数）
            long days = ChronoUnit.DAYS.between(hotelOrderInfoVO.getCreateTime(), LocalDateTime.now());
            if (days < 1) {
                return ApiResult.error("房间已被预定");
            }
        }

        // 计算订单金额（默认购买1晚，考虑折扣因素）
        HotelRoomVO hotelRoomVO = hotelRoomVOS.get(0);
        BigDecimal amount = DecimalUtils.calculateTotal(
                1,
                hotelRoomVO.getPrice(),
                (hotelRoomVO.getDiscount() == null ? 1 : (hotelRoomVO.getDiscount() / 10))
        );

        // 填充订单业务字段
        hotelOrderInfo.setAmount(amount);
        hotelOrderInfo.setUserId(LocalThreadHolder.getUserId());  // 从线程上下文获取用户ID
        hotelOrderInfo.setCreateTime(LocalDateTime.now());       // 设置订单创建时间
        hotelOrderInfo.setPayStatus(false);                      // 默认未支付状态

        hotelOrderInfoMapper.save(hotelOrderInfo);
        return ApiResult.success();
    }

    /**
     * 修改民宿订单信息
     * 
     * 根据传入的订单实体对象更新数据库记录，要求订单ID必须已存在。该操作会修改订单的
     * 所有非空字段，调用前需确保参数对象的完整性
     *
     * @param hotelOrderInfo 包含更新字段的民宿订单实体对象，必须包含有效的订单ID，
     *                       字段包括但不限于：订单状态、入住时间、房型信息等业务数据
     * @return Result<Void> 统一响应结果对象，操作成功时返回带有成功标识的Result，
     * 失败时返回包含错误信息的Result
     */
    @Override
    public Result<Void> update(HotelOrderInfo hotelOrderInfo) {
        // 执行数据库更新操作（根据MyBatis Mapper规范，空字段将不被更新）
        hotelOrderInfoMapper.update(hotelOrderInfo);

        // 返回标准成功响应（根据ApiResult规范自动封装响应码和消息）
        return ApiResult.success();
    }

    /**
     * 批量删除民宿订单
     *
     * @param ids 需要删除的民宿订单ID列表，不可为空列表
     * @return Result<Void> 通用返回结果，包含操作成功或失败的信息（Void表示无返回数据）
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行批量删除操作（根据方法名推断底层是批量更新订单状态为删除状态）
        hotelOrderInfoMapper.batchDelete(ids);
        // 返回标准成功响应
        return ApiResult.success();
    }

    /**
     * 查询民宿订单
     * 根据查询条件分页获取订单信息列表，并返回总记录数用于分页处理
     *
     * @param dto 查询条件实体，包含分页参数、入住时间、订单状态等过滤条件
     * @return Result<List < HotelOrderInfoVO>> 包含订单信息列表和总记录数的结果对象，
     * data为当前页数据列表，totalCount字段为符合条件的所有记录数
     */
    @Override
    public Result<List<HotelOrderInfoVO>> query(HotelOrderInfoQueryDto dto) {
        // 先查询符合条件的总记录数（用于分页组件计算总页数）
        Integer totalCount = hotelOrderInfoMapper.queryCount(dto);

        // 执行分页查询获取当前页数据列表
        List<HotelOrderInfoVO> result = hotelOrderInfoMapper.query(dto);

        // 封装包含分页信息的标准响应结构
        return ApiResult.success(result, totalCount);
    }


    /**
     * 查询供应商名下的民宿订单
     * 
     * 实现逻辑：
     * 1. 获取当前登录用户关联的供应商信息
     * 2. 校验供应商资质和状态
     * 3. 获取供应商管理的民宿及房间信息
     * 4. 基于房间信息查询关联的订单数据
     *
     * @param dto 订单查询参数对象，包含分页参数和过滤条件
     * @return Result<List < HotelOrderInfoVO>> 包含分页结果的响应对象，数据为酒店订单信息VO列表
     */
    @Override
    public Result<List<HotelOrderInfoVO>> queryVendorHotelOrder(HotelOrderInfoQueryDto dto) {
        // 供应商信息获取与校验
        // 通过线程上下文获取当前用户ID，并查询关联的供应商信息
        Integer userId = LocalThreadHolder.getUserId();
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(userId);
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        // 供应商存在性检查
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商信息异常");
        }

        // 供应商状态双重校验
        VendorVO vendorVO = vendorVOS.get(0);
        if (!vendorVO.getStatus() || !vendorVO.getIsAudit()) {
            return ApiResult.error("供应商状态异常或无查看权限");
        }

        // 民宿与房间信息获取
        // 查询供应商管理的所有民宿信息
        HotelQueryDto hotelQueryDto = new HotelQueryDto();
        hotelQueryDto.setVendorId(vendorVO.getId());
        List<HotelVO> hotelVOS = hotelMapper.query(hotelQueryDto);
        if (hotelVOS.isEmpty()) {
            return ApiResult.error("名下无管理民宿");
        }

        // 提取民宿ID集合用于后续查询
        List<Integer> hotelIds = hotelVOS.stream()
                .map(HotelVO::getId)
                .collect(Collectors.toList());

        // 订单数据查询
        // 根据民宿房间ID集合查询关联订单
        List<HotelRoomVO> hotelRoomVOS = hotelRoomMapper.queryByHotelIds(hotelIds);
        List<Integer> hotelRoomIds = hotelRoomVOS.stream()
                .map(HotelRoomVO::getId)
                .collect(Collectors.toList());

        // 构造复合查询参数（包含原始查询条件）
        HotelOrderInfoQueryParamDto paramDto = new HotelOrderInfoQueryParamDto(hotelRoomIds, dto);

        // 执行分页查询并返回结果
        List<HotelOrderInfoVO> orderInfoVOList = hotelOrderInfoMapper.queryByHotelRoomIds(paramDto);
        Integer totalCount = hotelOrderInfoMapper.queryCountByHotelRoomIds(paramDto);

        return ApiResult.success(orderInfoVOList, totalCount);
    }

    /**
     * 按天查询酒店订单数据并生成销售报表图表
     *
     * @param day 统计天数范围（例如：7表示最近7天）
     * @return Result<List < ChartVO>> 包含每日销售数据的图表视图对象列表，
     * 每个ChartVO对应一天的销售数据可视化信息
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        // 处理时间范围参数
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建酒店订单查询参数
        HotelOrderInfoQueryDto dto = new HotelOrderInfoQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());

        // 调用供应商订单查询接口
        Result<List<HotelOrderInfoVO>> hotelOrderVendor = queryVendorHotelOrder(dto);

        // 处理接口返回数据
        ApiResult<List<HotelOrderInfoVO>> response = (ApiResult<List<HotelOrderInfoVO>>) hotelOrderVendor;
        List<HotelOrderInfoVO> data = response.getData();

        // 转换数据格式：提取金额和时间字段
        List<MoneyDto> moneyDtoList = data.stream().map(hotelOrderInfoVO -> new MoneyDto(
                hotelOrderInfoVO.getAmount(),
                hotelOrderInfoVO.getPayTime()
        )).collect(Collectors.toList());

        // 生成每日销售报表
        List<ChartVO> chartVOS = MoneyUtils.generateDailySalesReport(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 根据指定天数查询销售额并生成每日报表图表数据
     *
     * @param day 查询天数范围（例如：7表示查询最近7天的数据）
     * @return 包含每日销售额图表数据的结果对象，图表数据按天分组统计
     */
    @Override
    public Result<List<ChartVO>> daysQueryMoney(Integer day) {
        // 获取时间范围参数
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建订单查询条件
        HotelOrderInfoQueryDto dto = new HotelOrderInfoQueryDto();
        dto.setStartTime(queryDto.getStartTime());
        dto.setEndTime(queryDto.getEndTime());

        // 查询原始订单数据
        List<HotelOrderInfoVO> orderInfoVOS = hotelOrderInfoMapper.query(dto);

        // 转换订单数据为金额数据集
        List<MoneyDto> moneyDtoList = orderInfoVOS.stream()
                .map(hotelOrderInfoVO -> new MoneyDto(
                        hotelOrderInfoVO.getAmount(),
                        hotelOrderInfoVO.getPayTime()
                )).collect(Collectors.toList());

        // 生成每日销售报表图表数据
        List<ChartVO> chartVOS = MoneyUtils.generateDailySalesReport(day, moneyDtoList);
        return ApiResult.success(chartVOS);
    }

    /**
     * 民宿订单支付处理
     * 
     * 该方法用于完成以下核心操作：
     * 1. 更新订单支付状态为已支付
     * 2. 记录当前系统时间为支付时间
     * 3. 将更新后的订单信息持久化到数据库
     *
     * @param hotelOrderInfo 需要支付的民宿订单信息对象，包含订单基础数据和业务字段
     * @return Result<Void> 统一响应结果对象，表示操作成功状态（无具体业务数据返回）
     */
    @Override
    public Result<Void> pay(HotelOrderInfo hotelOrderInfo) {
        // 更新订单支付状态及时间
        hotelOrderInfo.setPayStatus(true);
        hotelOrderInfo.setPayTime(LocalDateTime.now());

        // 执行数据库更新操作
        hotelOrderInfoMapper.update(hotelOrderInfo);

        return ApiResult.success();
    }
}
