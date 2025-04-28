package com.example.service.impl;


import com.example.mapper.ScenicCategoryMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicCategoryQueryDto;
import com.example.pojo.entity.ScenicCategory;
import com.example.service.ScenicCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点分类的业务逻辑实现类
 */
@Service
public class ScenicCategoryServiceImpl implements ScenicCategoryService {

    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;

    /**
     * 新增景点分类
     * 将传入的景点分类实体持久化到数据库，返回操作结果
     *
     * @param scenicCategory 景点分类实体对象，需包含分类名称、排序字段等必要属性（非空校验由调用方保证）
     * @return Result<Void> 操作结果对象，包含成功标识和状态码信息。成功时data字段为空
     */
    @Override
    public Result<Void> save(ScenicCategory scenicCategory) {
        // 调用Mapper层执行数据库插入操作
        scenicCategoryMapper.save(scenicCategory);
        // 构造成功响应（默认携带200状态码）
        return ApiResult.success();
    }

    /**
     * 修改景点分类信息
     * <p>
     * 通过持久层更新景点分类数据，要求实体对象必须包含有效ID
     *
     * @param scenicCategory 包含更新数据的景点分类实体对象，其id字段必须有效且对应现存记录
     * @return Result<Void> 统一响应结果，包含操作成功状态码（200）或失败错误码
     */
    @Override
    public Result<Void> update(ScenicCategory scenicCategory) {
        // 执行持久层更新操作（根据ORM框架约定，自动根据实体ID更新对应字段）
        scenicCategoryMapper.update(scenicCategory);
        // 返回统一封装的响应结果
        return ApiResult.success();
    }

    /**
     * 批量删除景点分类
     *
     * @param ids 需要删除的景点分类ID集合，支持传入多个分类ID进行批量操作
     * @return Result<Void> 包含操作结果的响应对象，Void表示成功时无具体数据返回
     * 失败时会通过错误码和错误信息说明具体原因
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行批量删除操作
        scenicCategoryMapper.batchDelete(ids);
        // 构造并返回成功响应
        return ApiResult.success();
    }

    /**
     * 根据查询条件分页查询景点分类列表
     * 通过组合查询条件DTO获取总记录数和分页数据列表
     *
     * @param dto 包含查询条件的传输对象，支持分页参数和分类筛选条件
     * @return Result<List < ScenicCategory>> 包含景点分类列表和总条数的响应对象，
     * 其中data属性为当前页数据集合，total属性为符合条件的总记录数
     */
    @Override
    public Result<List<ScenicCategory>> query(ScenicCategoryQueryDto dto) {
        // 获取符合查询条件的总记录数
        Integer totalCount = scenicCategoryMapper.queryCount(dto);

        // 执行分页查询获取当前页数据
        List<ScenicCategory> result = scenicCategoryMapper.query(dto);

        // 构建包含分页信息的响应对象
        return ApiResult.success(result, totalCount);
    }

}
