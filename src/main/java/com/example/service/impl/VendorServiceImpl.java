package com.example.service.impl;


import com.example.context.LocalThreadHolder;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.em.RoleEnum;
import com.example.pojo.entity.Vendor;
import com.example.pojo.vo.VendorVO;
import com.example.service.VendorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 供应商的业务逻辑实现类
 */
@Service
public class VendorServiceImpl implements VendorService {

    @Resource
    private VendorMapper vendorMapper;

    /**
     * 新增供应商信息并执行业务校验
     *
     * @param vendor 供应商实体对象，包含需要新增的供应商详细信息。需包含申请人基本信息、
     *               企业资质等必要字段，字段完整性由调用方保证
     * @return Result<Void> 操作结果对象，成功时data字段为空，失败时包含错误提示信息。
     * 具体场景：
     * - 重复申请时返回"请勿重复申请"
     * - 数据持久化成功返回成功状态
     */
    @Override
    public Result<Void> save(Vendor vendor) {
        // 校验用户是否已有申请记录（根据当前用户ID查询）
        VendorQueryDto queryDto = new VendorQueryDto();
        queryDto.setUserId(LocalThreadHolder.getUserId());
        Integer count = vendorMapper.queryCount(queryDto);
        if (count != 0) {
            return ApiResult.error("请勿重复申请");
        }

        // 初始化供应商基础信息
        vendor.setCreateTime(LocalDateTime.now());  // 设置创建时间为当前系统时间
        vendor.setStatus(true);  // 设置供应商状态为有效（底层数据默认值补充）

        /* 权限逻辑分支：
         * - 普通用户：绑定当前用户ID，设置未审核状态
         * - 管理员：不绑定用户ID（保留传入值），设置自动审核通过
         */
        if (!Objects.equals(LocalThreadHolder.getRoleId(),
                RoleEnum.ADMIN.getRole())) {
            vendor.setUserId(LocalThreadHolder.getUserId());  // 申请人身份绑定
            vendor.setIsAudit(false);  // 需管理员后续审核
        } else {
            vendor.setIsAudit(true);  // 管理员操作直接生效
        }

        vendorMapper.save(vendor);
        return ApiResult.success();
    }

    /**
     * 修改供应商信息
     * <p>
     * 调用数据访问层接口更新数据库中的供应商记录，并返回标准化操作结果。
     * 该方法要求传入完整的供应商实体对象，通过主键ID定位需要更新的目标记录。
     *
     * @param vendor 供应商实体对象，必须包含有效的供应商ID字段及需要更新的字段信息，不可为null
     * @return 标准化响应结果对象。操作成功时返回code=200的Result对象，
     * 若参数校验失败或数据库操作异常将由全局拦截器处理返回对应错误信息
     */
    @Override
    public Result<Void> update(Vendor vendor) {
        vendorMapper.update(vendor);
        return ApiResult.success();
    }

    /**
     * 批量删除供应商数据
     * <p>
     * 本方法通过供应商ID列表执行批量删除操作，调用底层mapper执行数据库删除操作，
     * 最终返回标准封装结果对象
     *
     * @param ids 要删除的供应商ID集合，允许传入多个ID进行批量操作。ID应为有效存在的供应商标识，
     *            调用方需自行保证ID列表的有效性和合法性
     * @return Result<Void> 统一响应结果对象，包含操作状态标识。成功时返回状态码200及成功标识，
     * 失败时返回对应的错误状态码和提示信息（具体错误处理取决于mapper层实现）
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行供应商数据批量删除操作
        vendorMapper.batchDelete(ids);

        // 构造并返回成功响应对象
        return ApiResult.success();
    }

    /**
     * 供应商分页查询接口
     *
     * @param dto 查询条件DTO，包含供应商名称、状态等过滤条件及分页参数
     * @return Result<List < VendorVO>> 包含两个数据元素：
     * 1. data: 供应商VO列表，展示供应商基本信息
     * 2. total: 符合条件的数据总数，用于分页计算
     */
    @Override
    public Result<List<VendorVO>> query(VendorQueryDto dto) {
        // 统计符合条件供应商总数（不受分页参数影响）
        Integer totalCount = vendorMapper.queryCount(dto);

        // 执行分页查询获取供应商列表（受分页参数限制）
        List<VendorVO> result = vendorMapper.query(dto);

        // 组合分页结果：将列表数据和总条数封装到统一响应结构
        return ApiResult.success(result, totalCount);
    }
}
