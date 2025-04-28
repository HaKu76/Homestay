package com.example.service.impl;


import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicRatingMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicRatingQueryDto;
import com.example.pojo.entity.ScenicRating;
import com.example.pojo.vo.ScenicRatingVO;
import com.example.service.ScenicRatingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点评分的业务逻辑实现类
 */
@Service
public class ScenicRatingServiceImpl implements ScenicRatingService {

    @Resource
    private ScenicRatingMapper scenicRatingMapper;

    /**
     * 新增景点评分（用户维度限制单次）
     *
     * @param scenicRating 景点评分实体对象，需包含景点ID、评分值等核心数据
     *                     - scenicId 必填，对应被评分的景点ID
     *                     - 其他评分相关字段根据业务需求定义
     * @return Result<Void> 操作结果包装对象
     * - 成功时返回"评分成功"提示
     * - 重复评分时返回"已经评分过了"错误提示
     */
    @Override
    public Result<Void> save(ScenicRating scenicRating) {
        // 构建复合查询条件：当前用户+当前景点
        ScenicRatingQueryDto scenicRatingQueryDto = new ScenicRatingQueryDto();
        scenicRatingQueryDto.setScenicId(scenicRating.getScenicId());
        scenicRatingQueryDto.setUserId(LocalThreadHolder.getUserId());

        // 校验用户是否已存在评分记录
        Integer queryCount = scenicRatingMapper.queryCount(scenicRatingQueryDto);
        if (queryCount != 0) {
            return ApiResult.error("已经评分过了");
        }

        /* 填充创建信息 */
        // 从线程上下文获取当前用户ID
        scenicRating.setUserId(LocalThreadHolder.getUserId());
        // 设置服务端时间作为创建时间
        scenicRating.setCreateTime(LocalDateTime.now());

        // 执行持久化操作
        scenicRatingMapper.save(scenicRating);
        return ApiResult.success("评分成功");
    }

    /**
     * 查询景点评分信息
     * <p>
     * 根据查询条件获取分页形式的景点评分数据，包含总记录数用于分页计算。查询结果将包含符合条件的所有评分记录，
     * 并返回统一封装的结果对象
     *
     * @param dto 景点评分查询数据传输对象，包含查询条件、分页参数（页码、页大小）等过滤条件
     * @return Result<List < ScenicRatingVO>> 统一响应结果对象，其中：
     * - data: 符合条件的景点评分VO列表
     * - total: 符合条件的所有记录总数（用于分页计算）
     */
    @Override
    public Result<List<ScenicRatingVO>> query(ScenicRatingQueryDto dto) {
        // 执行分页查询：先获取符合条件的总记录数，再获取当前页数据列表
        Integer totalCount = scenicRatingMapper.queryCount(dto);
        List<ScenicRatingVO> result = scenicRatingMapper.query(dto);

        // 封装包含分页信息的响应结果（数据列表+总记录数）
        return ApiResult.success(result, totalCount);
    }
}
