package com.example.service.impl;


import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.em.RoleEnum;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.SelectedVO;
import com.example.pojo.vo.VendorVO;
import com.example.service.ScenicService;
import com.example.utils.TextUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 景点的业务逻辑实现类
 */
@Service
public class ScenicServiceImpl implements ScenicService {

    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private VendorMapper vendorMapper;

    /**
     * 新增景点（供应商/管理员共用接口）
     *
     * @param scenic 景点实体对象，包含需要新增的景点数据
     * @return Result<Void> 操作结果，包含成功标识或错误信息。成功时data字段为空，
     * 失败时返回具体的错误提示（如供应商身份异常）
     */
    @Override
    public Result<Void> save(Scenic scenic) {
        // 初始化景点基础信息
        scenic.setStatus(true);
        scenic.setCreateTime(LocalDateTime.now());

        // 管理员操作分支：直接保存传入的完整数据
        if (Objects.equals(LocalThreadHolder.getRoleId(), RoleEnum.ADMIN.getRole())) {
            scenicMapper.save(scenic);
            return ApiResult.success();
        }

        // 供应商操作分支：验证并关联供应商信息
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        // 供应商身份验证（要求必须存在且唯一）
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商身份异常");
        }

        // 绑定供应商信息后保存
        scenic.setVendorId(vendorVOS.get(0).getId());
        scenicMapper.save(scenic);
        return ApiResult.success();
    }

    /**
     * 修改景点信息
     *
     * @param scenic 包含更新信息的景点实体对象，需携带完整的主键信息
     * @return Result<Void> 操作结果对象，包含操作成功标识或错误信息
     */
    @Override
    public Result<Void> update(Scenic scenic) {
        // 执行数据库更新操作
        scenicMapper.update(scenic);
        // 封装API成功响应
        return ApiResult.success();
    }

    /**
     * 批量删除指定ID列表对应的景点信息
     * <p>
     * 该方法通过数据访问层执行批量删除操作，删除成功后返回统一封装结果。
     * 若传入空列表或无效ID将不会执行删除操作。
     *
     * @param ids 待删除的景点ID列表（至少包含一个有效整数ID）
     * @return Result<Void> 包含操作结果的状态对象，成功时data字段为null，
     * 失败时包含错误状态码和信息（根据项目统一异常处理机制返回）
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行批量删除操作（通过MyBatis mapper实现数据库交互）
        scenicMapper.batchDelete(ids);

        // 返回统一封装的API成功响应（根据项目规范处理成功/异常状态）
        return ApiResult.success();
    }


    /**
     * 分页查询景点信息
     *
     * @param dto 包含分页参数、景点筛选条件的查询传输对象。需包含页码(pageNo)、
     *            每页数量(pageSize)等分页参数，以及可选的景点名称、区域等筛选条件
     * @return 包含分页结果的响应对象。其中：
     * - data: 当前页的景点VO列表，已按业务要求转换格式
     * - total: 符合条件的总记录数，用于前端分页组件展示
     * - 其他标准响应字段(code/msg)由基础Result对象提供
     */
    @Override
    public Result<List<ScenicVO>> query(ScenicQueryDto dto) {
        // 执行分页总数查询
        Integer totalCount = scenicMapper.queryCount(dto);

        // 获取当前页数据，查询结果已自动应用分页参数
        List<ScenicVO> result = scenicMapper.query(dto);

        // 封装标准响应格式，携带分页元数据
        return ApiResult.success(result, totalCount);
    }


    /**
     * 供应商查询自身名下关联的景点信息
     *
     * @param dto 景点查询条件对象，包含筛选景点所需的各种参数
     * @return 包含景点VO列表的响应结果对象。当供应商身份异常时返回错误信息，
     * 正常情况返回符合查询条件的景点列表
     */
    @Override
    public Result<List<ScenicVO>> queryVendorScenic(ScenicQueryDto dto) {
        // 构建供应商查询条件：从线程上下文获取当前用户ID
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());

        // 执行供应商查询（根据系统设计，每个用户最多对应一个供应商账户）
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);

        /*
         * 供应商身份校验：
         * 1. 用户未关联供应商账户的情况
         * 2. 正常情况应仅存在一个供应商账户
         */
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商身份异常");
        }

        // 将查询到的供应商ID注入景点查询条件
        dto.setVendorId(vendorVOS.get(0).getId());

        // 执行带供应商ID的景点查询
        return query(dto);
    }


    /**
     * 查询可关联景点下拉选项列表
     * <p>
     * 函数通过构造查询条件获取可用景点数据，并将其转换为下拉选择器需要的精简格式。
     * 查询条件强制限定景点状态为启用状态(status=true)，确保只返回有效景点。
     *
     * @return Result<List < SelectedVO>> 包含景点ID和名称的下拉选项列表结果对象。
     * SelectedVO结构：id-景点唯一标识，name-景点名称
     */
    @Override
    public Result<List<SelectedVO>> querySelectedScenic() {
        // 构建景点查询条件对象，强制指定启用状态
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setStatus(true);

        // 执行数据库查询获取基础数据
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 将完整景点数据转换为下拉选择器需要的精简结构
        List<SelectedVO> selectedScenicList = scenicVOS.stream()
                .map(scenicVO -> new SelectedVO(
                        scenicVO.getId(),
                        scenicVO.getName()
                )).collect(Collectors.toList());

        return ApiResult.success(selectedScenicList);
    }


    /**
     * 处理用户浏览景点操作，记录浏览用户信息
     *
     * @param scenicId 需要记录浏览操作的景点唯一标识(ID)
     * @return Result<Void> 统一响应结果，包含操作成功状态（无具体数据返回）
     */
    @Override
    public Result<Void> viewOperation(Integer scenicId) {
        // 查询景点基础信息
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setId(scenicId);
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 当景点不存在时直接返回成功
        if (scenicVOS.isEmpty()) {
            return ApiResult.success();
        }

        // 获取已记录浏览用户列表
        ScenicVO scenicVO = scenicVOS.get(0);
        String viewIds = scenicVO.getViewIds();

        // 检查当前用户是否已存在于浏览记录中
        if (TextUtils.exitId(viewIds, LocalThreadHolder.getUserId())) {
            return ApiResult.success();
        }

        // 生成新的浏览用户ID列表
        String newViewIds = TextUtils.join(viewIds, LocalThreadHolder.getUserId());

        // 构建并执行景点信息更新
        Scenic scenic = new Scenic();
        scenic.setId(scenicId);
        scenic.setViewIds(newViewIds);
        scenicMapper.update(scenic);

        return ApiResult.success();
    }


    /**
     * 执行景点收藏/取消收藏操作
     *
     * @param scenicId 景点ID（必须存在的有效ID）
     * @return Result<Void> 包含操作结果状态的响应对象：
     * - 成功时返回操作提示（"收藏成功"或"取消收藏成功"）
     * - 当景点不存在时直接返回成功状态
     */
    @Override
    public Result<Void> saveOperation(Integer scenicId) {
        // 通过景点ID查询景点基础信息
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setId(scenicId);
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 处理空结果情况：当景点不存在时提前返回
        if (scenicVOS.isEmpty()) {
            return ApiResult.success();
        }

        // 获取当前用户ID和景点收藏用户列表
        ScenicVO scenicVO = scenicVOS.get(0);
        String saveIds = scenicVO.getSaveIds();
        Integer userId = LocalThreadHolder.getUserId();

        // 构建更新实体：根据用户是否已收藏决定新增/移除用户ID
        Scenic scenic = new Scenic();
        scenic.setId(scenicVO.getId());
        scenic.setSaveIds(
                TextUtils.exitId(saveIds, userId) ?
                        TextUtils.split(saveIds, userId) :  // 存在则分割移除
                        TextUtils.join(saveIds, userId)     // 不存在则拼接添加
        );

        // 执行数据库字段更新
        scenicMapper.updateSaveIds(scenic);

        // 根据操作类型返回对应提示信息
        return ApiResult.success(TextUtils.exitId(saveIds, userId) ? "取消收藏成功" : "收藏成功");
    }


    /**
     * 查询当前用户对指定景点的收藏状态
     *
     * @param scenicId 需要查询的景点唯一标识ID
     * @return 包含布尔值的结果对象：<br>
     * - true表示用户已收藏该景点<br>
     * - false表示用户未收藏该景点<br>
     * - 当景点不存在时返回空成功结果
     */
    @Override
    public Result<Boolean> saveStatus(Integer scenicId) {
        // 构建景点查询条件
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        scenicQueryDto.setId(scenicId);

        // 执行景点信息查询
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 处理空结果集情况（景点可能不存在）
        if (scenicVOS.isEmpty()) {
            return ApiResult.success();
        }

        // 解析查询结果中的收藏用户ID列表
        ScenicVO scenicVO = scenicVOS.get(0);
        String saveIds = scenicVO.getSaveIds();

        // 验证当前用户是否存在于收藏列表
        return ApiResult.success(TextUtils.exitId(saveIds, LocalThreadHolder.getUserId()));
    }


    /**
     * 查询用户收藏的景点信息
     * <p>
     * 实现逻辑：
     * 1. 构建景点查询DTO对象
     * 2. 从线程上下文获取当前用户ID
     * 3. 将用户ID设置为查询条件中的收藏标识
     * 4. 执行数据库查询获取收藏景点视图对象集合
     *
     * @return Result<List < ScenicVO>> 包含用户收藏景点数据的响应结果对象，数据为景点视图对象集合
     */
    @Override
    public Result<List<ScenicVO>> querySave() {
        // 构建基础查询条件对象
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();

        // 从线程局部存储中获取当前认证用户ID
        Integer userId = LocalThreadHolder.getUserId();
        // 将用户ID设置为收藏过滤条件（根据数据库设计可能需要转换为字符串格式）
        scenicQueryDto.setSaveIds(String.valueOf(userId));

        // 执行数据库查询，获取符合收藏条件的景点视图对象列表
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);

        // 将结果封装为统一响应格式
        return ApiResult.success(scenicVOS);
    }

}
