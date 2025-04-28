package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicStrategyMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicStrategyQueryDto;
import com.example.pojo.entity.ScenicStrategy;
import com.example.pojo.vo.ScenicStrategyListVO;
import com.example.pojo.vo.ScenicStrategyVO;
import com.example.service.ScenicStrategyService;
import com.example.utils.TextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 景点攻略的业务逻辑实现类
 */
@Service
public class ScenicStrategyServiceImpl implements ScenicStrategyService {

    @Resource
    private ScenicStrategyMapper scenicStrategyMapper;

    /**
     * 新增景点攻略信息
     *
     * @param scenicStrategy 待新增的景点攻略实体对象，需包含攻略内容、景点ID等必要字段
     * @return Result<Void> 操作结果，成功时返回状态码200和成功消息，失败时包含错误信息
     */
    @Override
    public Result<Void> save(ScenicStrategy scenicStrategy) {
        // 初始化实体基础信息
        scenicStrategy.setCreateTime(LocalDateTime.now());
        scenicStrategy.setUserId(LocalThreadHolder.getUserId());

        // 设置默认审核状态
        scenicStrategy.setIsAudit(false);

        // 持久化数据操作
        scenicStrategyMapper.save(scenicStrategy);
        return ApiResult.success();
    }

    /**
     * 更新景点攻略信息
     * 根据实体ID全量更新攻略信息，需确保参数对象包含有效主键
     *
     * @param scenicStrategy 包含更新信息的景点攻略实体对象（必须包含有效ID）
     * @return Result<Void>操作结果对象，包含执行状态和提示信息
     */
    @Override
    public Result<Void> update(ScenicStrategy scenicStrategy) {
        // 执行数据库更新操作，基于MyBatis的update方法实现
        scenicStrategyMapper.update(scenicStrategy);

        // 返回标准化成功响应
        return ApiResult.success();
    }

    /**
     * 批量删除景点攻略
     * <p>
     * 根据提供的ID列表批量删除对应的景点攻略数据
     *
     * @param ids 需要删除的景点攻略ID列表（不可为null或空列表）
     * @return Result<Void> 操作结果，成功时包含空数据体
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicStrategyMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 查询景点攻略信息
     *
     * @param dto 查询条件对象，包含分页参数、筛选条件等字段
     * @return Result<List < ScenicStrategyVO>> 响应对象，其中：
     * - data字段: 当前页的景点攻略数据列表
     * - total字段: 符合条件的所有记录总数（用于分页）
     */
    @Override
    public Result<List<ScenicStrategyVO>> query(ScenicStrategyQueryDto dto) {
        // 查询符合条件的数据总条数（忽略分页参数）
        Integer totalCount = scenicStrategyMapper.queryCount(dto);

        // 执行分页查询获取当前页数据（包含分页参数）
        List<ScenicStrategyVO> result = scenicStrategyMapper.query(dto);

        return ApiResult.success(result, totalCount);
    }

    /**
     * 审核景点攻略
     * <p>
     * 将指定ID的景点攻略标记为已审核状态
     *
     * @param id 待审核的景点攻略唯一标识符，不可为空
     * @return Result<Void> 包含操作结果的响应对象：
     * - 成功时返回携带成功消息的Result<Void>
     * - 失败时返回对应错误信息
     */
    @Override
    public Result<Void> audit(Integer id) {
        // 创建实体并更新审核状态
        ScenicStrategy scenicStrategy = new ScenicStrategy();
        scenicStrategy.setId(id);
        scenicStrategy.setIsAudit(true);
        scenicStrategyMapper.update(scenicStrategy);

        return ApiResult.success("景点攻略审核成功");
    }

    /**
     * 查询已审核通过的攻略列表（带分页信息）
     *
     * @param dto 查询参数对象，方法内部会强制设置isAudit=true
     *            包含分页参数（pageNo/pageSize）和其他过滤条件
     * @return Result<List < ScenicStrategyListVO>> 包含分页结果的标准响应对象：
     * data字段为处理后的攻略列表数据（ScenicStrategyListVO集合）
     * total字段为符合条件的数据总条数
     */
    @Override
    public Result<List<ScenicStrategyListVO>> queryList(ScenicStrategyQueryDto dto) {
        // 强制设置只查询审核通过的内容
        dto.setIsAudit(true);

        // 执行总数统计和分页查询
        Integer totalCount = scenicStrategyMapper.queryCount(dto);
        List<ScenicStrategyVO> scenicStrategyVOS = scenicStrategyMapper.query(dto);

        // 转换VO并处理富文本内容
        List<ScenicStrategyListVO> scenicStrategyListVOS = new ArrayList<>();
        for (ScenicStrategyVO scenicStrategyVO : scenicStrategyVOS) {
            ScenicStrategyListVO scenicStrategyListVO = new ScenicStrategyListVO();
            BeanUtils.copyProperties(scenicStrategyVO, scenicStrategyListVO);

            // 从富文本中提取纯文本，并截取前200字符作为摘要
            String detail = TextUtils.extractText(scenicStrategyVO.getContent(), 200);
            scenicStrategyListVO.setDetail(detail);

            scenicStrategyListVOS.add(scenicStrategyListVO);
        }

        // 返回包含分页信息的标准响应
        return ApiResult.success(scenicStrategyListVOS, totalCount);
    }
}
