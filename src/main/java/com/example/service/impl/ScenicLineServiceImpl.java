package com.example.service.impl;

import com.example.mapper.ScenicLineMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicLineQueryDto;
import com.example.pojo.entity.ScenicLine;
import com.example.pojo.vo.ScenicLineVO;
import com.example.service.ScenicLineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点路线的业务逻辑实现类
 */
@Service
public class ScenicLineServiceImpl implements ScenicLineService {

    @Resource
    private ScenicLineMapper scenicLineMapper;

    /**
     * 新增景点路线实体并持久化到数据库
     *
     * @param scenicLine 景点路线实体对象，方法执行时会自动注入创建时间字段。
     *                   需要包含路线名称、景点列表等业务必要字段
     * @return Result<Void> 包含操作结果的响应对象，成功时返回状态码200和成功标识，
     * 失败时包含错误状态码和提示信息
     */
    @Override
    public Result<Void> save(ScenicLine scenicLine) {
        // 设置实体创建时间为当前系统时间
        scenicLine.setCreateTime(LocalDateTime.now());

        // 执行数据库持久化操作
        scenicLineMapper.save(scenicLine);

        return ApiResult.success();
    }

    /**
     * 修改景点路线信息
     *
     * @param scenicLine 包含更新信息的景点路线实体对象，需要包含有效的路线ID和需要修改的字段数据，
     *                   实体对象的非空字段将被更新到数据库记录中
     * @return 返回操作结果包装对象，包含以下状态：
     * - 成功状态（200）及提示信息（当更新成功时）
     * - 错误状态（400）及错误详情（当发生数据库操作异常时）
     */
    @Override
    public Result<Void> update(ScenicLine scenicLine) {
        // 执行数据库更新操作，根据实体对象ID更新对应记录
        scenicLineMapper.update(scenicLine);
        return ApiResult.success();
    }

    /**
     * 批量删除景点路线数据
     *
     * @param ids 需要删除的景点路线ID集合，不可为null或空集合
     * @return Result<Void> 包含操作结果的响应实体，成功时data字段为null
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicLineMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 根据查询条件获取景点路线分页数据
     *
     * @param dto 查询条件对象，包含分页参数、过滤条件等字段
     *            （需包含pageNo/pageSize分页参数）
     * @return 包含分页结果的响应对象，其中：
     * - data: 当前页的景点路线数据列表
     * - total: 符合条件的数据总条数（用于分页计算）
     */
    @Override
    public Result<List<ScenicLineVO>> query(ScenicLineQueryDto dto) {
        // 获取符合条件的数据总数（不包含分页参数）
        Integer totalCount = scenicLineMapper.queryCount(dto);

        // 执行分页查询（会自动应用dto中的pageNo/pageSize参数）
        List<ScenicLineVO> result = scenicLineMapper.query(dto);

        // 构建包含分页信息的响应对象
        return ApiResult.success(result, totalCount);
    }
}
